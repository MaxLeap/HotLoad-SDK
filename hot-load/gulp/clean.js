"use strict";

var gulp = require("gulp");
var del = require("del");

function deleteTask(glob, next) {
    del(glob, null, next);
}

var sdkCleanList = ["sdk/bin/**/*", "!sdk/bin/.*"];

gulp.task("clean-sdk", function(next) { deleteTask(sdkCleanList, next); });

gulp.task("clean", ["clean-sdk"]);
