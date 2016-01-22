//Copyright (C) 2011 by Jimmy Cuadra
/*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*/

define([], function () {
  var shellwords = {
    scan: function (string, pattern, callback) {
      var match, result;
      result = "";
      while (string.length > 0) {
        match = string.match(pattern);
        if (match) {
          result += string.slice(0, match.index);
          result += callback(match);
          string = string.slice(match.index + match[0].length);
        } else {
          result += string;
          string = "";
        }
      }
      return result;
    },

    split: function (line) {
      var field, words;
      if (line == null) {
        line = "";
      }
      words = [];
      field = "";
      this.scan(line, /\s*(?:([^\s\\\'\"]+)|'((?:[^\'\\]|\\.)*)'|"((?:[^\"\\]|\\.)*)"|(\\.?)|(\S))(\s|$)?/, function (match) {
        var dq, escape, garbage, raw, seperator, sq, word;
        raw = match[0], word = match[1], sq = match[2], dq = match[3], escape = match[4], garbage = match[5], seperator = match[6];
        if (garbage != null) {
          throw new Error("Unmatched quote");
        }
        field += word || (sq || dq || escape).replace(/\\(?=.)/, "");
        if (seperator != null) {
          words.push(field);
          return field = "";
        }
      });
      if (field) {
        words.push(field);
      }
      return words;
    },

    escape: function (str) {
      if (str == null) {
        str = "";
      }
      if (str == null) {
        return "''";
      }
      return str.replace(/([^A-Za-z0-9_\-.,:\/@\n])/g, "\\$1").replace(/\n/g, "'\n'");
    }
  };
  return shellwords;
});