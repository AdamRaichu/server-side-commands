const fs = require("fs");
var v = process.env.VERSION_TXT;
fs.readFile("gradle.properties", "utf8", (err, old) => {
  if (err) throw err;
  fs.writeFile("gradle.properties", old.replace("$version", v), "utf8", (err) => {
    if (err) throw err;
    console.log("gradle.properties succesfully written");
    console.log(old.replace("$version", v));
    return;
  });
});
