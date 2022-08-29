
Vue.prototype.$http = axios;
var orgApi = Vue.resource('/api/org/get/2302')

Vue.component('data-row', {
    props: ['block'],
    template: '<div><i>{{block.id}}</i>\tНаименование:\t{{block.name}}\tТип:\t{{block.type}}</div>'
})

Vue.component('custom-data', {
    props: ['blocks'],
    template:
        '<div>' +
            '<data-row v-for="block in blocks" :key="block.id" :block="block"/>' +
        '</div>',
    /*created: function () {
        orgApi.get().then(result =>
            result.json().then(data => {
                    console.log(data);
                    this.blocks.push(data)
                }
            )
        )
    }*/
    created: function () {
        var vm = this;
        axios.get('/api/org/get/2302').then(response =>
        {
            console.log(response);
            vm.blocks.push(response.data)
        });
    }
});

var app = new Vue({
    el: '#cdata',
    template: '<custom-data :blocks="blocks"/>',
    data: {
        blocks: []
    }
});