function initializeChemBaseTable()
{

    Vue.component('chembase-table',
    {
        props: ['rows',
                'columns',
                'titles',
                'currentpage',
                'elementsperpage',
                'filterfields',
                'tablefilters',
                'exporthandler',
                'popuphandler',
                'showbottombar',
                'editbuttonicon',
                'deletebuttonicon',
                'editbuttontitle',
                'deletebuttontitle'],
        
        /* --------------------------------------------------------------------------------------- */

        data: function()
        {
            return { sortColumn: '', ascending: true }
        },

        /* --------------------------------------------------------------------------------------- */

        computed:
        {
            filteredRows: function()
            {
                var self = this;
                return self.rows.filter(function (row)
                {
                    var filtersPassed = true;

                    for (var key of Object.keys(self.tablefilters))
                    {
                        if (row[key].indexOf(self.tablefilters[key]) == -1)
                        {
                            filtersPassed = false;
                            break;
                        }
                    }
                    return filtersPassed;
                });
            }
        },

        /* --------------------------------------------------------------------------------------- */

        methods:
        {
            sortTable: function(col)
            {
              if (this.sortColumn === col)
              {
                  this.ascending = !this.ascending;
              }
              else
              {
                  this.ascending = true;
                  this.sortColumn = col;
              }

              var ascending = this.ascending;

              this.filteredRows.sort(function(a, b)
              {
                  if (a[col] > b[col])
                  {
                      return ascending ? 1 : -1
                  }
                  else if (a[col] < b[col])
                  {
                      return ascending ? -1 : 1
                  }
                  return 0;
              })
            },

            numPages: function()
            {
                return Math.ceil(this.filteredRows.length / this.elementsperpage);
            },

            getRows: function()
            {
                var start = (this.currentpage-1) * this.elementsperpage;
                var end = start + this.elementsperpage;
                return this.filteredRows.slice(start, end);
            },

            changePage: function(page)
            {
                if (page > this.numPages())
                {
                    this.currentpage = page - 1;
                }
                else if(page == 0)
                {
                    this.currentpage = 1;
                }
                else
                {
                    this.currentpage = page;
                }
            },
            
            updateFilter: function(filterID, filterValue)
            {
                this.tablefilters[filterID] = filterValue;
            }
        },

        /* --------------------------------------------------------------------------------------- */

        template: `

          <div class="chembase-table-div">
                <table class="chembase-table">

                  <thead>
                        <tr class="chembase-table-toolbar">
                            <th v-bind:colspan="columns.length+1">
                              <button class="chembase-table-toolbar-button" v-on:click="popuphandler('ADD', null)">NEW</button>

                              <!-- Constructing table filtering fields -->
                              <span v-for="field in filterfields">
                                  <span class="chembase-table-toolbar-label">{{field.label}}: </span>
                                  <select size="1" v-model="field['value']" v-on:change="updateFilter(field.id, field.value)">
                                      <option v-bind:value="currentval.selectval" v-for="currentval in field['selectvalues']">
                                          {{currentval.displayval}}
                                      </option>
                                  </select>
                              </span>
                            </th>
                        </tr>

                        <tr>
                              <th class="chembase-table-column-header" v-for="column in columns" v-on:click="sortTable(column)">{{titles[column]}}
                                  <div class="arrow" v-if="column == sortColumn" v-bind:class="[ascending ? 'arrow_up' : 'arrow_down']"></div>
                              </th>
                              <th class="chembase-table-column-header"><!--Empty header for buttons--></th>
                          </tr>
                </thead>

                      <tbody>
                          <tr v-for="row in getRows()">
                              <td v-for="column in columns">{{row[column]}}</td>
                              <td class="chembase-table-buttons-column">
                                  <span class="chembase-table-edit-button"   v-bind:title=editbuttontitle    v-bind:style="{ backgroundImage: 'url(' + editbuttonicon + ')' }"   v-on:click="popuphandler('EDIT', row)"></span>
                                  <span class="chembase-table-delete-button" v-bind:title=deletebuttontitle  v-bind:style="{ backgroundImage: 'url(' + deletebuttonicon + ')'}"  v-on:click="popuphandler('DELETE', row)"></span>
                              </td>
                          </tr>
                      </tbody>

                      <tfoot>
                          <tr v-if="showbottombar" class="chembase-table-bottombar">
                              <td v-bind:colspan="columns.length+1">
                                <div><button v-on:click="exporthandler(filteredRows)">EXPORT</button></div>
                              </td>
                          </tr>
                      </tfoot>

                  </table>

                  <div class="chembase-table-pagination">
                      <div class="chembase-table-page-number-switch" v-on:click="changePage(numPages())"> >> </div>
                      <div class="chembase-table-page-number-switch" v-on:click="changePage(currentpage+1)"> > </div>
                      <div class="chembase-table-page-number">{{currentpage}}/{{numPages()}} </div>
                      <div class="chembase-table-page-number-switch" v-on:click="changePage(currentpage-1)"> < </div>
                      <div class="chembase-table-page-number-switch" v-on:click="changePage(1)"> << </div>
                  </div>

          </div>
        `

        /* --------------------------------------------------------------------------------------- */

    });

}
