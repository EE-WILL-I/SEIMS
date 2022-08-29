function httpGetAsync(url)
{
    let resp = "";
    fetch(url).then(function(response) {
        return response.json();
    }).then(function(data) {
        resp = data;
    }).catch(function() {
        resp = "Server Error";
    });
    return resp;
}