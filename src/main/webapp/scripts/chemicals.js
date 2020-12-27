
/* ------------------------------------------------------------------------------------------ */

function constructChemicalsSearchForm()
{
    chemicalsSearchForm = new Vue
    ({
        el: '#chemicalsSearchForm',
        data:
        {
            visible: true,
            searchButtonMessage: "SEARCH",

            fields:
            [
                { id: 'label_chemical_name',        type: 'label',          value: 'Chemical Name' },
                { id: 'label_storage_location',     type: 'label',          value: 'Storage Location' },
                { id: 'label_manufacturer',         type: 'label',          value: 'Manufacturer' },
                { id: 'label_supplier',             type: 'label',          value: 'Supplier' },
                { id: 'chemical_name',              type: 'textfield',      value: '' },
                { id: 'storage_location',           type: 'select',         value: '', selectvalues: [ {selectval: '', displayval: ''} ] },
                { id: 'manufacturer',               type: 'textfield',      value: '' },
                { id: 'supplier',                   type: 'textfield',      value: '' }
            ]
        },

        methods:
        {
            searchButtonClickHandler: function()
            {               
                var chemicalName    = chemicalsSearchForm.fields[4].value;
                var storageLocation = chemicalsSearchForm.fields[5].value;              
                var manufacturer    = chemicalsSearchForm.fields[6].value;
                var supplier        = chemicalsSearchForm.fields[7].value;

                if (typeof storageLocation === 'undefined' || storageLocation == null || storageLocation.length == 0)
                    storageLocation = 0;

                displayChemBaseProgressDialog();
                performSearch(chemicalName, storageLocation, manufacturer, supplier);
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function constructChemicalsTable()
{
    chemicalsTable = new Vue
    ({
        el: '#chemicalsTable',
        data:
        {
            visible: true,
            currentPage: 1,
            elementsPerPage: 20,
            showBottomBar: true,

            editButtonIcon: 'resources/edit.svg',
            deleteButtonIcon: 'resources/remove.svg',
            editButtonTitle: 'Edit',
            deleteButtonTitle: 'Delete',

            tableFilters: {},
            filterFields: [],

            // Titles contains a list of column titles for each column
            // -------------------------------------------------------
            titles:
            {
                id: "ID",
                chemicalName: "CHEMICAL NAME",
                bruttoFormula: "BRUTTO FORMULA",
                moralMass: "MOLAR MASS",
                quantity: "QUANTITY",
                unit: "UNIT",
                storageLocation: "STORAGE LOCATION",
                manufacturer: "MANUFACTURER",
                supplier: "SUPPLIER",
                dateOfEntry: "ENTRY DATE",
                additionalInfo: "ADDITIONAL INFO"
            },

            // This list contains all the columns that will be displayed on screen
            // -------------------------------------------------------------------
            columns: ['id', 'chemicalName', 'bruttoFormula', 'moralMass', 'quantity', 'unit', 'storageLocation'],

            // Rows contains an array with data in form of objects and their properties
            // ------------------------------------------------------------------------
            rows: [],
        },

        methods:
        {
            exportHandler: function(rows)
            {
                JSONToCSVConverter(rows, "Chemicals", true);
            },

            popupHandler: function(operation, row)
            {
                var fields = null;          
                var titleMessage = null;

                if (operation == 'ADD')
                {
                    titleMessage = "ADD CHEMICAL";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: '' },
                        { id: 'chemicalName', label: 'Chemical Name', type: 'textfield', value: '' },
                        { id: 'bruttoFormula', label: 'Brutto Formula', type: 'textfield', value: '' },
                        { id: 'molarMass', label: 'Molar Mass', type: 'textfield', value: '' },
                        { id: 'quantity', label: 'Quantity', type: 'textfield', value: '' },
                        {
                            id: 'unit',
                            label: 'Unit',
                            type: 'select',
                            value: '',
                            selectvalues:
                            [
                                { selectval: '',    displayval: ''  },
                                { selectval: 'g',   displayval: 'g' },
                                { selectval: 'kg',  displayval: 'kg' },
                                { selectval: 'ml',  displayval: 'ml' },
                                { selectval: 'l',   displayval: 'l' },
                                { selectval: 'cm3', displayval: 'cm3' },
                                { selectval: 'dm3', displayval: 'dm3' },
                                { selectval: 'mol', displayval: 'mol' },
                                { selectval: 'mmol', displayval: 'mmol' }
                            ]
                        },
                        {
                            id: 'storageLocation',
                            label: 'Storage Location',
                            type: 'select',
                            value: 0,
                            selectvalues: JSON.parse(sessionStorage.getItem("selectableLocations"))
                        },
                        { id: 'manufacturer', label: 'Manufacturer', type: 'textfield', value: '' },
                        { id: 'supplier', label: 'Supplier', type: 'textfield', value: '' },
                        { id: 'dateOfEntry', label: 'Entry Date', type: 'textfield-noneditable', value: '' },
                        { id: 'additionalInfo', label: 'Additional Info', type: 'textarea', value: '' },
                    ];
                    displayChemicalsAddEditDialog(titleMessage, fields);
                }
                else if (operation == 'EDIT')
                {
                    titleMessage = "EDIT CHEMICAL";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: row.id },
                        { id: 'chemicalName', label: 'Chemical Name', type: 'textfield', value: row.chemicalName },
                        { id: 'bruttoFormula', label: 'Brutto Formula', type: 'textfield', value: row.bruttoFormula },
                        { id: 'molarMass', label: 'Molar Mass', type: 'textfield', value: row.moralMass },
                        { id: 'quantity', label: 'Quantity', type: 'textfield', value: row.quantity },
                        {
                            id: 'unit',
                            label: 'Unit',
                            type: 'select',
                            value: row.unit,
                            selectvalues:
                            [
                                { selectval: '',    displayval: ''  },
                                { selectval: 'g',   displayval: 'g' },
                                { selectval: 'kg',  displayval: 'kg' },
                                { selectval: 'ml',  displayval: 'ml' },
                                { selectval: 'l',   displayval: 'l' },
                                { selectval: 'cm3', displayval: 'cm3' },
                                { selectval: 'dm3', displayval: 'dm3' },
                                { selectval: 'mol', displayval: 'mol' },
                                { selectval: 'mmol', displayval: 'mmol' }
                            ]
                        },
                        {
                            id: 'storageLocation',
                            label: 'Storage Location',
                            type: 'select',
                            value: getLocationCode(row.storageLocation),
                            selectvalues: JSON.parse(sessionStorage.getItem("selectableLocations"))
                        },
                        { id: 'manufacturer', label: 'Manufacturer', type: 'textfield', value: row.manufacturer },
                        { id: 'supplier', label: 'Supplier', type: 'textfield', value: row.supplier },
                        { id: 'dateOfEntry', label: 'Entry Date', type: 'textfield-noneditable', value: row.dateOfEntry },
                        { id: 'additionalInfo', label: 'Additional Info', type: 'textarea', value: row.additionalInfo },
                    ];
                    displayChemicalsAddEditDialog(titleMessage, fields);
                }
                else
                {
                    var pKey = row.id;
                    displayChemicalsDeleteDialog("Are you sure you want to delete the selected chemical?", pKey);
                }
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayChemicalsAddEditDialog(providedTitle, providedFields)
{
    if (chemicalsAddEditDialog != null)
    {
        chemicalsAddEditDialog.title = providedTitle;
        chemicalsAddEditDialog.fields = providedFields;        
        chemicalsAddEditDialog.visible = true;
        return;
    }

    chemicalsAddEditDialog = new Vue
    ({
        el: '#chemicalsAddEditDialog',
        data:
        {
            visible: true,
            title: providedTitle,
            yesButtonMessage: "OK",
            noButtonMessage: "CANCEL",
            fields: providedFields               
        },

        methods:
        {
            yesButtonClickHandler: function()
            {
                var id              = this.fields[0].value;
                var chemicalName    = this.fields[1].value;
                var bruttoFormula   = this.fields[2].value;
                var molarMass       = this.fields[3].value;
                var quantity        = this.fields[4].value;
                var unit            = this.fields[5].value;
                var storageLocation = this.fields[6].value;
                var manufacturer    = this.fields[7].value;
                var supplier        = this.fields[8].value;
                var dateOfEntry     = this.fields[9].value;
                var additionalInfo  = this.fields[10].value;

                if (typeof chemicalName === 'undefined' || chemicalName == null || chemicalName.trim().length == 0)
                    displayChemBaseNotificationDialog("Enter Chemical Name!");

                else if (typeof bruttoFormula === 'undefined' || bruttoFormula == null || bruttoFormula.trim().length == 0)
                    displayChemBaseNotificationDialog("Enter Brutto Formula!");

                else if (typeof molarMass === 'undefined' || molarMass == null || molarMass.trim().length == 0)
                    displayChemBaseNotificationDialog("Enter Molar Mass!");

                else if (typeof quantity === 'undefined' || quantity == null || quantity.length == 0 || quantity <= 0)
                    displayChemBaseNotificationDialog("Enter Quantity!");

                else if (typeof unit === 'undefined' || unit == null || unit.length == 0)
                    displayChemBaseNotificationDialog("Select unit!");

                else if (typeof storageLocation === 'undefined' || storageLocation == null || storageLocation.length == 0 || storageLocation <= 0)
                    displayChemBaseNotificationDialog("Enter storage location!");

                else
                {
                    if (this.title == 'ADD CHEMICAL')
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendAddChemicalRequest(chemicalName,
                                               bruttoFormula,
                                               molarMass,
                                               quantity,
                                               unit,
                                               storageLocation,
                                               manufacturer,
                                               supplier,
                                               additionalInfo)
                    }
                    else
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendEditChemicalRequest(id,
                                                chemicalName,
                                                bruttoFormula,
                                                molarMass,
                                                quantity,
                                                unit,
                                                storageLocation,
                                                manufacturer,
                                                supplier,
                                                additionalInfo);
                    }
                }
            },

            noButtonClickHandler: function()
            {
                this.visible = false;
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayChemicalsDeleteDialog(providedMessage, pKey)
{
    if (chemicalsDeleteDialog != null)
    {
        chemicalsDeleteDialog.message = providedMessage;
        chemicalsDeleteDialog.primaryKey = pKey;
        chemicalsDeleteDialog.visible = true;
        return;
    }

    chemicalsDeleteDialog = new Vue
    ({
        el: '#chemicalsDeleteDialog',
        data:
        {
            visible: true,
            title: "DELETE CHEMICAL",
            yesButtonMessage: "OK",
            noButtonMessage: "CANCEL",
            message: providedMessage,
            primaryKey: pKey
        },
        methods:
        {
            yesButtonClickHandler: function()
            {
                this.visible = false;
                displayChemBaseProgressDialog();
                sendDeleteChemicalRequest(this.primaryKey);
            },

            noButtonClickHandler: function()
            {
                this.visible = false;
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */
/*                                      HTTP METHODS                                          */
/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */

function repeatLastSearch()
{
    request.open('POST', BASE_URL + '/ChemBase/v1/chemicals/search', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(sessionStorage.getItem("lastSearchParams"));

    request.onreadystatechange = processSearchResponse;
}

/* ------------------------------------------------------------------------------------------ */

function performSearch(chemicalName, storageLocation, manufacturer, supplier)
{
    var data = JSON.stringify
    ({
        "chemicalName": chemicalName,
        "storageLocation": storageLocation,
        "manufacturer": manufacturer,
        "supplier": supplier
    });

    /* saving search params to session storage */
    sessionStorage.setItem("lastSearchParams", data);

    request.open('POST', BASE_URL + '/ChemBase/v1/chemicals/search', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processSearchResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processSearchResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            chemicalsTable.rows = responseData['chemicals'];            
        }
        else if (request.status == 400)
        {
            var responseData = JSON.parse(request.responseText);
            var errorMessage = responseData['error'];

            if (typeof errorMessage === 'undefined' || errorMessage == null || errorMessage == 'INVALID_SESSION_ID')
                window.location.href="index.html";
            else
                displayChemBaseNotificationDialog(errorMessage);
        }
        else
        {
            window.location.href="index.html";
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */

function sendDeleteChemicalRequest(chemicalID)
{
    var data = JSON.stringify({ "id": chemicalID });    
    request.open('POST', BASE_URL + '/ChemBase/v1/chemicals/delete', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);
    request.onreadystatechange = function() { processDeleteChemicalResponse(chemicalID); }
}

/* ------------------------------------------------------------------------------------------ */

function processDeleteChemicalResponse(chemicalID)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            repeatLastSearch();
        }
        else if (request.status == 400)
        {
            var responseData = JSON.parse(request.responseText);
            var errorMessage = responseData['error'];
                
            if (typeof errorMessage === 'undefined' || errorMessage == null || errorMessage == 'INVALID_SESSION_ID')
                window.location.href="index.html";
            else
                displayChemBaseNotificationDialog(errorMessage);
        }
        else
        {
            window.location.href="index.html";
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */

function sendAddChemicalRequest(chemicalName,
                                bruttoFormula,
                                molarMass,
                                amount,
                                unit,
                                storageLocation,
                                manufacturer,
                                supplier,
                                additionalInfo)
{
    var data = JSON.stringify
    ({
        "chemicalName":     chemicalName,
        "bruttoFormula":    bruttoFormula,
        "molarMass":        molarMass,
        "amount":           amount,
        "unit":             unit,
        "storageLocation":  storageLocation,
        "manufacturer":     manufacturer,
        "supplier":         supplier,
        "additionalInfo":   additionalInfo
    });

    request.open('POST', BASE_URL + '/ChemBase/v1/chemicals/add', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);
    
    request.onreadystatechange = processAddChemicalResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processAddChemicalResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            console.log("HTTP 200");
            var responseData = JSON.parse(request.responseText);
            var addedChemical = responseData['chemical'];               
            chemicalsTable.rows.unshift(addedChemical);
        }
        else if (request.status == 400)
        {
            var responseData = JSON.parse(request.responseText);
            var errorMessage = responseData['error'];
                
            if (typeof errorMessage === 'undefined' || errorMessage == null || errorMessage == 'INVALID_SESSION_ID')
                window.location.href="index.html";
            else
                displayChemBaseNotificationDialog(errorMessage);
        }
        else
        {
            window.location.href="index.html";
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */

function sendEditChemicalRequest(chemicalID,
                                 chemicalName,
                                 bruttoFormula,
                                 molarMass,
                                 amount,
                                 unit,
                                 storageLocation,
                                 manufacturer,
                                 supplier,
                                 additionalInfo)
{
    var data = JSON.stringify
    ({
        "id":               chemicalID,
        "chemicalName":     chemicalName,
        "bruttoFormula":    bruttoFormula,
        "molarMass":        molarMass,
        "amount":           amount,
        "unit":             unit,
        "storageLocation":  storageLocation,
        "manufacturer":     manufacturer,
        "supplier":         supplier,
        "additionalInfo":   additionalInfo
    });

    request.open('POST', BASE_URL + '/ChemBase/v1/chemicals/edit', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);
    
    request.onreadystatechange = processEditChemicalResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processEditChemicalResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;
        if (request.status == 200)
        {
            repeatLastSearch();
        }
        else if (request.status == 400)
        {
            var responseData = JSON.parse(request.responseText);
            var errorMessage = responseData['error'];
                
            if (typeof errorMessage === 'undefined' || errorMessage == null || errorMessage == 'INVALID_SESSION_ID')
                window.location.href="index.html";
            else
                displayChemBaseNotificationDialog(errorMessage);
        }
        else
        {
            window.location.href="index.html";
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */
/*                                      MISCELLANEOUS                                         */
/* ------------------------------------------------------------------------------------------ */
/* ------------------------------------------------------------------------------------------ */

function getLocationCode(locationString)
{
    var locations = JSON.parse(sessionStorage.getItem("selectableLocations"))
    for (var i = 0; i < locations.length; i++)
    {
        if (locations[i].displayval == locationString)
        {
            return locations[i].selectval;
        }
    }

    return 0;   
}

/* ------------------------------------------------------------------------------------------ */
