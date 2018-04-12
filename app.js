(function() {
    'use strict';
    var app = {
        pinnedHashes: []
    };
    app.start = function() {
        $.getJSON('/api/tree', function(tree){
            for(let child of tree.children) {

                var pinButton = $('<td><button class="mdl-button mdl-js-button mdl-button--icon">'+
                                 '<i class="material-icons">offline_pin</i>'+
                                  '</button></td>');

                var downloadButton = $('<td><button class="mdl-button mdl-js-button mdl-js-ripple-effect">'+
                                      'Download'+
                                       '</button></td>');

                var row = $(
                            '<tr>'+
                            '<td class="mdl-data-table__cell--non-numeric">' + child.name + '</td>' +
                            '<td>25</td>' +
                            '<td>' + child.size + '</td>' +
                            '</tr>'
                            );

                row.prepend(pinButton);
                row.append(downloadButton);
                downloadButton.on('click', function(){
                    console.log(child.hash);
                    app.getFile(child.name, child.hash);
                });
                pinButton.on('click', function(){
                    app.pinFile(child.hash);
                })
                $('#test').append(row);
            }
        });
    }

    app.installService = function() {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker
                     .register('./worker.js')
                     .then(function() { console.log('Service Worker Registered'); });
          }
    }

    app.setupDB = function() {
        if (!('indexedDB' in window)) {
            console.log('This browser doesn\'t support IndexedDB');
            return;
        }

        var rq = indexedDB.open('cekt-filesync-data', 1);
        rq.onupgradeneeded = function(event) {
            var db = event.target.result;
            if (!db.objectStoreNames.contains('files')) {
                db.createObjectStore('files');
            }
        }
        rq.onsuccess = function(event) {
            var db = event.target.result;
            var transaction = db.transaction(["files"], "readwrite");
            transaction.objectStore("files").getAllKeys().onsuccess = function(event) {
                app.pinnedHashes = event.target.result;
                console.log(app.pinnedHashes);
            }

        }


    }

    app.pinFile = function(hash) {

        var xhr = new XMLHttpRequest();
        xhr.open("GET", "/api/file/" + hash, true);
        xhr.responseType = "blob";
        xhr.addEventListener("load", function () {
            if (xhr.status === 200) {
                console.log(hash + " retrieved");
                var blob = xhr.response;
                app.putFile(hash, blob);
                app.pinnedHashes.push(hash);
                console.log(app.pinnedHashes);
            }
        }, false);
        xhr.send();

    }

    app.putFile = function(hash, blob) {
        var request = indexedDB.open('cekt-filesync-data', 1, function(db) {
                              if (!db.objectStoreNames.contains('files')) {
                                db.createObjectStore('files');
                              }
                            });

        request.onsuccess = function (event) {
            console.log("Success creating/accessing IndexedDB database");
            var db = request.result;
            var transaction = db.transaction(["files"], "readwrite");
            transaction.objectStore("files").put(blob, hash);
        };

        request.onerror = function (event) {
            console.log("Error creating/accessing IndexedDB database");
        };
    }

    app.getFile = function(name, hash) {

        if (app.pinnedHashes.includes(hash)){
            var request = indexedDB.open('cekt-filesync-data', 1);
            request.onsuccess = function (event) {
                console.log("Success creating/accessing IndexedDB database");
                var db = request.result;
                var transaction = db.transaction(["files"], "readwrite");
                transaction.objectStore("files").get(hash).onsuccess = function (event) {
                    var blob = event.target.result;
                    var URL = window.URL;
                    var blobUrl = URL.createObjectURL(blob);

                    $('#testl').attr('download', name);
                    $('#testl').attr('href', blobUrl);
                    $('#testl').click();
                };
            }
        } else {
                $('#testl').attr('download', name);
                $('#testl').attr('href', '/api/file/' + hash);
                $('#testl').click();
        }
    }

    app.start();
    app.installService();
    app.setupDB();
})();




