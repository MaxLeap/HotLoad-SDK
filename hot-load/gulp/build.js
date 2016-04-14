"use strict";

var gulp = require("gulp");

gulp.task("build-sdk", ["content-sdk", "scripts-sdk"]);

gulp.task("build", ["build-sdk"]);
