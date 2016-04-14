var gulp = require("gulp");
var install = require("gulp-install");
var path = require("path");
var runSequence = require("run-sequence");

gulp.task("install", function(done) {
    var packages = [
        path.join(__dirname, "..", "sdk", "package.json")
    ];
    return gulp.src(packages)
        .pipe(install());
});
