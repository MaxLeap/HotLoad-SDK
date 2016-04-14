var gulp = require("gulp");
var which = require("which");
var path = require("path");
var spawn = require("child_process").spawn;
var runSequence = require("run-sequence");

function linkPackage(folder, createBinLinks, callback) {
    if (typeof createBinLinks === "function") {
        callback = createBinLinks;
        createBinLinks = false;
    }

    which("npm", function(err, resolvedPath) {
        if (err) return callback(err);

        var args = ["link"];
        if (!createBinLinks) {
            args.push("--no-bin-links");
        }

        var link = spawn(resolvedPath, args, {cwd: folder});
        link.stdout.pipe(process.stdout);
        link.stderr.pipe(process.stderr);
        link.on("close", callback);
    });
}

function linkDependency(folder, sourcePackage, callback) {
    which("npm", function(err, resolvedPath) {
        if (err) return callback(err);

        var link = spawn(resolvedPath, ["link", sourcePackage], {cwd: folder});
        link.stdout.pipe(process.stdout);
        link.stderr.pipe(process.stderr);
        link.on("close", callback);
    });
}

gulp.task("link-sdk", ["content-sdk"], function(done) {
    linkPackage(path.join(__dirname, "..", "sdk", "bin"), done);
});

gulp.task("link", function(done) {
    runSequence("link-sdk", done);
});
