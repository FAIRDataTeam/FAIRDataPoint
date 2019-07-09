/*
 * The MIT License
 * Copyright © 2017 DTL
 *
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
var retrieveMetadata = function(url, callback) {
  $.get({
    url: url,
    headers: {
      "Accept": "application/ld+json"
    },
    success: function(data) {
      $.each(data["@graph"], function(index, graph) {
        if (graph["@id"] === url) {
          console.log('adding metadata', graph);
          callback(graph);
        }
      });
    }
  });
};

var normalizeArray = function(thing) {
  return thing == undefined ? [] :
    thing instanceof Array ? thing : [ thing ];
};

var getDateString = function(metadata, predicate) {
  return new Date(metadata[predicate]["@value"]).toDateString();
};

// translate the timestamp to a more readable date
var humanifyTimestamp = function(selector) {
  var element = $(selector);
  var original = element.text();
  var newString = new Date(original).toDateString();
  element.text(newString);
};

var trimText = function(str, limit) {
  var strlimit = limit || 128;
  var threshold = 8;
  
  if (str.length <= strlimit) {
    return str;
  }
  
  // limit scope to threshold
  var slice = str.slice(strlimit - threshold, strlimit + threshold);
  
  var slice_split = -1;
  
  for (var i = slice.length; i > 0; i--) {
    var char = slice.charAt(i);
    
    if (char == ' ' || char == '.') {
      slice_split = i;
      break;
    }
  }
  
  var split_point = (slice_split == -1) ? strlimit : strlimit - threshold + slice_split;
  
  return str.substring(0, split_point) + "...";
};

var RDF_ICON = CONTEXTPATH + "/images/rdf_w3c_icon.48";
var HTML_ICON = CONTEXTPATH + "/images/html-filetype.png";
var CSV_ICON = CONTEXTPATH + "/images/microsoft-excel.png";
var TXT_ICON = CONTEXTPATH + "/images/txt-filetype.png";
var UNKNOWN_ICON = CONTEXTPATH + "/images/unknown.png";

var iconMapping = {
  "text/turtle": RDF_ICON,
  "application/rdf+xml": RDF_ICON,
  "text/html": HTML_ICON,
  "text/csv": CSV_ICON,
  "text/plain": TXT_ICON
};

var getDistIcon = function(metadata) {
  var mediatypes = normalizeArray(metadata["dcat:mediaType"]);
  for (var mediatype of mediatypes) {
    if (iconMapping[mediatype]) {
      return iconMapping[mediatype];
    }
  }
  return UNKNOWN_ICON;
};

var addDistributionThing = function(metadata, parent) {
  var thing = $("<div>")
    .addClass("col-2 text-center above-panel-link");
  
  var img = $("<img>").attr("src", getDistIcon(metadata))
    .tooltip({title: metadata["dcterms:title"], placement: 'auto'});
  
  thing.append(img);
  
  $(parent || "#dist-list").append(thing);
};

var addCatalogPanel = function(metadata) {
  var datasets = normalizeArray(metadata["dcat:dataset"]);
  
  var panel = $("<div>")
    .addClass("col-5 rounded")
    .append($("<h3>").text(trimText(metadata["dcterms:title"])))
    .append($("<p>").text(trimText(metadata["dcterms:description"])))
    .append($("<span>").text("Datasets: " + datasets.length))
    .appendTo($("#catalog-panels"));
  
  panel.bind("click", function(e) {
    $("#catalog-title").text(metadata["dcterms:title"]);
    $("#catalog-description").text(metadata["dcterms:description"]);
    $("#catalog-issued").text(getDateString(metadata, "fdp:metadataIssued"));
    $("#catalog-license").attr("href", metadata["dcterms:license"]["@id"]);
    $("#catalog-modified").text(getDateString(metadata, "fdp:metadataModified"));

    // reset the datasets
    $("#dataset-panels").html("");

    $.each(datasets, function(index, dataset) {
      // populate the dataset panels
      retrieveMetadata(dataset["@id"], addDatasetPanel);
    });

    $("#catalog-detail").show();
  });
}; 

var addDatasetPanel = function(metadata) {
  var panel = $("<div>")
    .addClass("col-5 rounded clickable-panel")
    .append($("<h4>").text(trimText(metadata["dcterms:title"])))
    .append($("<p>").text(trimText(metadata["dcterms:description"])));
    
  var row = $("<div>").addClass("row");
  
  $("<div>").addClass("col text-left")
    .append($("<strong>").text("Published: "))
    .append(getDateString(metadata, "fdp:metadataIssued"))
    .appendTo(row);
  
  $("<div>").addClass("col text-center above-panel-link")
    .append($("<a>", {"href": metadata["dcterms:license"]["@id"]}).text("license"))
    .appendTo(row);
  
  $("<div>").addClass("col text-right")
    .append($("<strong>").text("Version: "))
    .append(metadata["dcterms:hasVersion"])
    .appendTo(row);
  
  panel.append(row);
  
  // make the panel as a while clickable, but links within the panel individually clickable as well
  // see https://stackoverflow.com/a/3494108/4627267
  var link = $("<a>").attr("href", metadata["@id"]);
  link.append($("<span>").addClass("panel-link"));
  panel.append(link);
  
  var distlist = $("<div>")
    .addClass("row justify-content-center")
    .appendTo(panel);
  
  var dists = normalizeArray(metadata["dcat:distribution"]);
  $.each(dists, function(index, dist) {
    retrieveMetadata(dist["@id"], function(meta) {
      addDistributionThing(meta, distlist);
    });
  });
  
  panel.appendTo($("#dataset-panels"));
};
