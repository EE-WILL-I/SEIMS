let progress = false;

const sleepUntil = async (f, timeoutMs) => {
    return new Promise((resolve, reject) => {
        const timeWas = new Date();
        const wait = setInterval(function() {
            if (f()) {
                //console.log("resolved after", new Date() - timeWas, "ms");
                clearInterval(wait);
                resolve();
            } else if (new Date() - timeWas > timeoutMs) { // Timeout
                //console.log("rejected after", new Date() - timeWas, "ms");
                clearInterval(wait);
                reject();
            }
        }, 20);
    });
}

function showLoadingWrapper ($obj, complete) {
   sleepUntil(() => progress === false, 400).then(() => {
       progress = true;
       var $loadingPanel = jQuery('<div class="loading_panel" id="lp_1"><img src="/img/loading.gif" class="loading_gif"/></div>');
       var $wrappedDiv = $obj.find('.wrapped_div');
       $loadingPanel.hide();
       $obj.append($loadingPanel);
       $wrappedDiv.fadeOut(50, function () {
           $loadingPanel.fadeIn(100, function () {
               $wrappedDiv.hide();
               if (typeof complete == 'function')
                   complete();
               progress = false;
           });
       });
   }).catch(() => { return false; })
}

function hideLoadingWrapper($obj, complete) {
    sleepUntil(() => progress === false, 400).then(() => {
        progress = true;
        var $wrappedDiv = $obj.find('.wrapped_div');
        var $lp = $obj.find('#lp_1');
        $lp.fadeOut(100, function () {
            $wrappedDiv.fadeIn(50, function () {
                $lp.remove();
                if (typeof complete == 'function')
                    complete();
                progress = false;
            });
        });
    }).catch(() => { return false; })
}