function initializeChemBaseOverlayForm()
{
    Vue.component('chembase-overlay-form',
    {
        props: ['title',
                'fields',
                'yesbuttonmessage',
                'nobuttonmessage',
                'yesbuttonclickhandler',
                'nobuttonclickhandler'],

        template: `

          <div class="chembase-add-edit-dialog">
              <div class="chembase-add-edit-dialog-content">
                <div class="chembase-add-edit-dialog-header">{{title}}</div>
                <div class="chembase-add-edit-dialog-body">

                    <div class="chembase-add-edit-dialog-row" v-for="field in fields">


                        <!-- Label -->
                        <!-- ----- -->
                        <span v-if="field['type'] != 'hidden'" class="chembase-add-edit-dialog-label">{{field['label']}}</span>


                        <!-- TextField non-editable -->
                        <!-- ---------------------- -->
                        <span v-if="field['type']=='textfield-noneditable'">
                          <input class="chembase-add-edit-dialog-textfield" type="text" v-model="field['value']" readonly>
                        </span>


                        <!-- TextField input -->
                        <!-- --------------- -->
                        <span v-if="field['type']=='textfield'">
                          <input class="chembase-add-edit-dialog-textfield" type="text" v-model="field['value']">
                        </span>


                        <!-- TextArea input -->
                        <!-- -------------- -->
                        <span v-if="field['type']=='textarea'">
                        <textarea class="chembase-add-edit-dialog-textarea" rows="5" v-model="field['value']"></textarea>
                        </span>


                        <!-- PasswordField input -->
                        <!-- ------------------- -->
                        <span v-if="field['type']=='password'">
                          <input class="chembase-add-edit-dialog-password" type="password" v-model="field['value']">
                        </span>


                        <!-- Select input -->
                        <!-- ------------ -->
                        <span v-if="field['type']=='select'">
                          <select class="chembase-add-edit-dialog-select" size="1" v-model="field['value']">
                            <option v-bind:value="currentval.selectval" v-for="currentval in field['selectvalues']">
                              {{currentval.displayval}}
                            </option>
                          </select>
                        </span>


                        <!-- Multiselect input -->
                        <!-- ----------------- -->
                        <div v-if="field['type']=='multiselect'" class="chembase-add-form-view-multiselect">
                          <div class="chembase-add-edit-dialog-multiselect-selectbox" v-on:click="field['displaycheckboxes'] = !field['displaycheckboxes'];">                 
                            <select>
                              <option>Select an option</option>
                            </select>
                            <div class="chembase-add-edit-dialog-multiselect-overselect"></div>
                          </div>
                          <div v-show="field['displaycheckboxes']" class="chembase-add-edit-dialog-multiselect-checkboxes" style="overflow-x: auto;">
                            <span v-for="currentval in field['selectvalues']" class="chembase-add-form-view-multiselect-entry">
                                <input class="chembase-add-edit-dialog-multiselect-value" type="checkbox" v-model="field['value']" v-bind:value="currentval.selectval"/>
                                <span class="chembase-add-edit-dialog-multiselect-label">{{currentval.displayval}}</span>
                            <br/>
                            </span>
                          </div>
                        </div>


                        <!-- Checkbox input -->
                        <!-- -------------- -->
                        <span v-if="field['type']=='checkbox'">
                          <input type="checkbox" v-model="field['value']" :checked="field['value']">
                          <span>{{field['label']}}</span><br/> 
                        </span>


                        <!-- Switch input -->
                        <!-- ------------ -->
                        <span v-if="field['type']=='switch'">
                          <label class="chembase-add-edit-dialog-switch">
                            <input type="checkbox" v-model="field['value']" :checked="field['value']">
                            <span class="chembase-add-edit-dialog-slider round"></span>
                          </label>
                        </span>


                        <!-- Radio Group -->
                        <!-- ----------- -->
                        <div class="chembase-add-edit-dialog-radiogroup" v-if="field['type']=='radiogroup'">
                          <span v-for="currentval in field['selectvalues']">
                            <input class="chembase-add-edit-dialog-radiogroup-value" type="radio" v-model="field['value']" v-bind:value="currentval.selectval"/>
                            <span class="chembase-add-edit-dialog-radiogroup-label">{{currentval.displayval}}</span>
                            <br/>
                          </span>
                        </div>
                

                        <!-- Checkbox Group -->
                        <!-- -------------- -->
                        <div class="chembase-add-edit-dialog-checkboxgroup" v-if="field['type']=='checkboxgroup'">
                          <span v-for="currentval in field['selectvalues']">
                            <input class="chembase-add-edit-dialog-checkboxgroup-value" type="checkbox" v-model="field['value']" v-bind:value="currentval.selectval" />
                            <span class="chembase-add-edit-dialog-checkboxgroup-label">{{currentval.displayval}}</span>
                            <br/>
                          </span>
                        </div>
  
                    </div>
                </div>
 
                <button class="chembase-add-edit-dialog-button" v-on:click="yesbuttonclickhandler()">{{yesbuttonmessage}}</button>
                <button class="chembase-add-edit-dialog-button" v-on:click="nobuttonclickhandler()">{{nobuttonmessage}}</button>

              </div>
          </div>

        `
    });
}
