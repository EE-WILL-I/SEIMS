const capital = 'Воронеж';
var xBias = -150;
var yBias = 0;
var isLoading = false;
var currDistr = '';

var showTitle = function (obj, map) {
    var box = obj.getBBox();
    var cls = obj.data('name') == '' ? 'class="empty"' : '';
    var $title = $('<div id="mapTitle" ' + cls + ' style="position: absolute; pointer-events:none;">' + obj.data('hoverName') + '<\/div>');
    $('#'+map).append($title);
    $title.css({
        top: box.y + box.height / 2 + (obj.data('offy') || 0) + yBias,
        left: (box.x + box.width / 2) - $title.innerWidth() + (obj.data('offx') || 0) + xBias
    });
};

var hideTitle = function () {
    $('#mapTitle').remove();
};
function clearPaths() {
    $('#map').children().remove();
}

function setMap(a_map, p_map) {
    $('#'+ p_map).css("display", "none");
    $('#' + a_map).css("display", "block");
    if(a_map === 'city_map') {
        xBias = -200;
        yBias = -100;
    } else {
        xBias = -150;
        yBias = 0;
    }
}

function setPaths(map, paths) {
    var w = 600;
    var h = 600;

    const r = Raphael(map, w, h),

        attributes = {
            fill: '#437A65',
            stroke: '#ccc',
            'stroke-width': 2,
            'stroke-linejoin': 'round'
        },

        arr = new Array();

    for (var region in paths) {
        var obj = r.path(paths[region].path);
        var regionColor = paths[region].p_color;
        obj.attr(attributes);
        obj.attr('fill', regionColor);
        arr[obj.id] = region;
        obj.data('name', paths[region].name);
        obj.data('hoverName', paths[region].hoverName);
        obj.data('offx', paths[region].offx);
        obj.data('offy', paths[region].offy);
        obj
            .hover(function () {
                if (paths[arr[this.id]].name != '') {
                    this.animate({
                        fill: paths[arr[this.id]].a_color
                    }, 300)
                }
                ;
                showTitle(this, map);
            }, function () {
                this.animate({
                    fill: paths[arr[this.id]].p_color
                }, 300);
                hideTitle();
            })
            .click(function () {
                if (paths[arr[this.id]].name == '') return false;
                let distr = paths[arr[this.id]].hoverName.split(" район")[0];
                if(distr === currDistr) return false;
                if (!isLoading) {
                    isLoading = true;
                    if (paths[arr[this.id]].hoverName === capital) {
                        setMap('city_map', 'map');
                        isLoading = false;
                        return false;
                    }
                    var $mapData = $('#map_data');
                    showLoadingWrapper($('#map_data_wrapper'), () => {
                        $('#district_data').remove();
                    });
                    fetch("https://" + window.location.host + "/open-api/map/districtData/" + distr).then(function (response) {
                        return response.json();
                    }).then(function (data) {
                        var $districtData = $('<div id="district_data"></div>')
                        for (const key in data) {
                            let out = '- ' + data[key].name + '<br/>';
                            var $org = $('<p class="org_link_wrapper"><a class="org_link" href="/org/get/' + data[key].id + '">' + out + '</a></p>');
                            $districtData.append($org);
                        }
                        hideLoadingWrapper($('#map_data_wrapper'), () => {
                            $mapData.append($districtData);
                            isLoading = false;
                        });
                        currDistr = distr;
                    }).catch(function () {
                        alert("Server Error. Cant fetch data of " + distr + " district from database.");
                        isLoading = false;
                    });
                    isLoading = false;
                }
            });
    }
    if(map === 'city_map') {
        $('#city_map').children('svg').children('path').each(function () {
            $(this).css( {'transform' : 'translate(-200px, -100px)' });
        })
    }
}

(function($){

    $(window).on("load", function () {
        if (!('fetch' in window)) {
            console.log('Fetch API not found, please upgrade your browser.');
            return;
        }

        $(".mapPoint").mouseenter(function(){
            $(this).children("span").show();
        }).mouseleave(function(){
            $(this).children("span").hide();
        });

        //setPaths(paths);
    });
})(jQuery);