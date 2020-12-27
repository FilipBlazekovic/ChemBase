function initializeChemBaseConfirmationDialog()
{
    Vue.component('chembase-confirmation-dialog',
    {
        props: ['title',
                'message',
                'yesbuttonmessage',
                'nobuttonmessage',
                'yesbuttonclickhandler',
                'nobuttonclickhandler'],
        
        template: `

          <div class="chembase-confirmation-dialog">
              <div class="chembase-confirmation-dialog-content">
              <div class="chembase-confirmation-dialog-header">{{title}}</div>
                <div class="chembase-confirmation-dialog-body">{{message}}</div>
                <button class="chembase-confirmation-dialog-button" v-on:click="yesbuttonclickhandler">{{yesbuttonmessage}}</button>
                <button class="chembase-confirmation-dialog-button" v-on:click="nobuttonclickhandler">{{nobuttonmessage}}</button>
              </div>
          </div>

        `
    });
}
