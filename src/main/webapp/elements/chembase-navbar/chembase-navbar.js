function initializeChemBaseNavbar()
{
    Vue.component('chembase-navbar',
    {
        props: ['buttons', 'buttonhandler'],

        template: `

            <div class="chembase-navbar">
                <span class="chembase-navbar-button" v-for="button in buttons" v-if="button.visible" v-bind:class="{ chembasenavbaractive: button.active }" v-on:click="buttonhandler(button.id)">{{button.text}}</span>
            </div>
        `
    });
 }