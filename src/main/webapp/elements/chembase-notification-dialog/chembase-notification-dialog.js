function initializeChemBaseNotificationDialog()
{
    Vue.component('chembase-notification-dialog',
    {
        props: ['title',
                'message',
                'buttonmessage',
                'buttonclickhandler'],
        
        template: `

          <div class="chembase-notification-dialog">
            <div class="chembase-notification-dialog-content">
              <div class="chembase-notification-dialog-header">{{title}}</div>
              <div class="chembase-notification-dialog-body">{{message}}</div>
              <button class="chembase-notification-dialog-button" v-on:click="buttonclickhandler">{{buttonmessage}}</button>
            </div>
          </div>

        `
    });
}
