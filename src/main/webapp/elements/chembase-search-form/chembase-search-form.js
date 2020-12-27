function initializeChemBaseSearchForm()
{

    Vue.component('chembase-search-form',
    {
        props: ['fields',
                'searchbuttonmessage',
                'searchbuttonclickhandler'],

        template: `
                <div class="chembase-search-form">
                    <div class="chembase-search-form-grid">
                        <div class="chembase-search-form-cell" v-for="field in fields">                 
                            
                            <!-- Label -->
                            <!-- ----- -->
                            <span class="chembase-search-form-label" v-if="field['type']=='label'">
                                {{field['value']}}:
                            </span>
                        
                            <!-- Textfield input -->
                            <!-- --------------- -->
                            <span v-if="field['type']=='textfield'">
                                <input class="chembase-search-form-textfield" type="text" v-model="field['value']" v-on:keyup.enter="searchbuttonclickhandler()">
                            </span>
                            
                            <!-- Select input -->
                            <!-- ------------ -->
                            <span v-if="field['type']=='select'">
                                <select class="chembase-search-form-select" size="1" v-model="field['value']" v-on:keyup.enter="searchbuttonclickhandler()">
                                    <option v-bind:value="currentval.selectval" v-for="currentval in field['selectvalues']">
                                        {{currentval.displayval}}
                                    </option>
                                </select>
                            </span>
                            
                        </div>
                    </div>
                    <button class="chembase-search-form-button" v-on:click="searchbuttonclickhandler()">{{searchbuttonmessage}}</button>
                </div>
            `
    });
}