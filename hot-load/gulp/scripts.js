"use strict";

var chmod = require("chmod");
var fs = require("fs");
var gulp = require("gulp");
var plugins = require("gulp-load-plugins")();
var through = require("through2");
var tsc = require("typescript");
var tsJsxLoader = require("ts-jsx-loader");
var merge = require("merge2");
var dtsGenerator = require("dts-generator");

var generatedDefinitionDependencies = {
    sdk: []
};

function tsJsxPipe(file, enc, cb) {
    var fileContent = file.contents.toString();
    file.contents = new Buffer(tsJsxLoader.call({cacheable: function() {} }, fileContent), enc);
    cb(null, file);
}

function scriptTask(cwd, jsx) {
    var options = {
        cwd: __dirname + "/../" + cwd,
        base: __dirname + "/../" + cwd
    };

    var generatedDefinitions = [
        "definitions/external/**/*.d.ts",
        "definitions/*.d.ts"
    ].concat(generatedDefinitionDependencies[cwd].map(function(dep) {
        return "definitions/generated/" + dep + ".d.ts";
    }));

    var tsProj = plugins.typescript.createProject("tsconfig.json", { typescript: tsc, declarationFiles: true });

    var fullReporter = plugins.typescript.reporter.fullReporter(/*fullFileName=*/ true);
    var errorCatch = through.obj();

    var tsResult = merge([
            gulp.src("{script,test,definitions}/**/*.ts", options),
            gulp.src(generatedDefinitions)])
        .pipe(plugins.if(jsx, through.obj(tsJsxPipe)))
        .pipe(plugins.typescript(tsProj, /* filterSettings=*/ undefined, {
            error: fullReporter.error,
            finish: function(results) {
                fullReporter.finish(results);
                if (results.syntaxErrors || results.globalErrors || results.semanticErrors || results.emitErrors) {
                    if (!process.env.WATCHING) {
                        errorCatch.emit("error", new plugins.util.PluginError("gulp-typescript", "TypeScript compilation failed"));
                    }
                }
            }
        }));

    return merge([
            tsResult.js.pipe(gulp.dest("bin", options)),
            tsResult.dts.pipe(gulp.dest("bin/definitions", options))
        ])
        .pipe(errorCatch);
}

function makeExecutable(path) {
    var contents = fs.readFileSync(path);
    fs.writeFileSync(path, "#!/usr/bin/env node\n" + contents);
    chmod(path, {execute: true});
}

gulp.task("scripts-external", ["tsd"]);

gulp.task("scripts-compile-sdk", ["scripts-external"], function() { return scriptTask("sdk"); });

gulp.task("scripts-dtsbundle-sdk", ["scripts-compile-sdk"], function () {
    dtsGenerator.generate({
        name: "hot-load",
        main: "hot-load/script/index",
        baseDir: "sdk/bin/definitions",
        files: ["script/acquisition-sdk.d.ts", "script/index.d.ts"],
        out: "definitions/generated/hot-load.d.ts"
    });
});

gulp.task("scripts-sdk", ["scripts-dtsbundle-sdk"]);
