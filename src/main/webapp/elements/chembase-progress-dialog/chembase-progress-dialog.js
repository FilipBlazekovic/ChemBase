function initializeChemBaseProgressDialog()
{
    Vue.component('chembase-progress-dialog',
    {
        props: ['title',
                'buttonmessage',
                'buttonclickhandler'],
                
        template: `
                <div class="chembase-progress-dialog">
                    <div class="chembase-progress-dialog-content">
                        <div class="chembase-progress-dialog-header">{{title}}</div>
                        <div class="chembase-progress-dialog-body">
                            <div class="chembase-progress-dialog-loader"></div>
                        </div>
                        <button class="chembase-progress-dialog-button" v-on:click="buttonclickhandler()">{{buttonmessage}}</button>
                    </div>
                </div>
        `
    });
}