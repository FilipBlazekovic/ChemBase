function initializeChemBaseOverlayFormSimple()
{
    Vue.component('chembase-overlay-form-simple',
    {
        props: ['title',
                'fields',
                'yesbuttonmessage',
                'nobuttonmessage',
                'yesbuttonclickhandler',
                'nobuttonclickhandler'],

        template: `

          <div class="chembase-add-edit-dialog-simple">
              <div class="chembase-add-edit-dialog-content-simple">
                <div class="chembase-add-edit-dialog-header-simple">{{title}}</div>
                <div class="chembase-add-edit-dialog-body-simple">

                    <div class="chembase-add-edit-dialog-row-simple" v-for="field in fields">


                        <!-- Label -->
                        <!-- ----- -->
                        <span v-if="field['type'] != 'hidden'" class="chembase-add-edit-dialog-label-simple">{{field['label']}}</span>


                        <!-- TextField non-editable -->
                        <!-- ---------------------- -->
                        <span v-if="field['type']=='textfield-noneditable'">
                            <input class="chembase-add-edit-dialog-textfield-simple" type="text" v-model="field['value']" readonly>
                        </span>


                        <!-- TextField input -->
                        <!-- --------------- -->
                        <span v-if="field['type']=='textfield'">
                            <input class="chembase-add-edit-dialog-textfield-simple" type="text" v-model="field['value']">
                        </span>


                        <!-- PasswordField input -->
                        <!-- ------------------- -->
                        <span v-if="field['type']=='password'">
                            <input class="chembase-add-edit-dialog-password-simple" type="password" v-model="field['value']">
                        </span>
  

                    </div>
                </div>
 
                <button class="chembase-add-edit-dialog-button-simple" v-on:click="yesbuttonclickhandler()">{{yesbuttonmessage}}</button>
                <button class="chembase-add-edit-dialog-button-simple" v-on:click="nobuttonclickhandler()">{{nobuttonmessage}}</button>

              </div>
          </div>

        `
    });
}
