(function() {
    'use strict';
    var app = {};
    app.start = function() {
                    $.getJSON('/api/tree', function(tree){
                        for(let child of tree.children) {
                            $('#test').append(
                            '<tr>'+
                            '<td class="mdl-data-table__cell--non-numeric">' + child.name + '</td>' +
                            '<td>25</td>' +
                            '<td>' + child.size + '</td>' +
                            '</tr>')
                        }
                    })
                }

    app.installService = function() {
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker
                     .register('./worker.js')
                     .then(function() { console.log('Service Worker Registered'); });
          }
    }
    app.start();
    app.installService();
})();




