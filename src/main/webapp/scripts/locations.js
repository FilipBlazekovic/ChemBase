
/* ------------------------------------------------------------------------------------------ */

function constructLocationsTable()
{
    locationsTable = new Vue
    ({
        el: '#locationsTable',
        data:
        {
            visible: false,
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
                location: "STORAGE LOCATION",
            },

            // This list contains all the columns that will be displayed on screen
            // -------------------------------------------------------------------
            columns: ['location'],

            // Rows contains an array with data in form of objects and their properties
            // ------------------------------------------------------------------------
            rows: [],
        },

        methods:
        {
            exportHandler: function(rows)
            {
                JSONToCSVConverter(rows, "Locations", true);
            },

            popupHandler: function(operation, row)
            {
                var fields = null;              
                var titleMessage = null;

                if (operation == 'ADD')
                {
                    titleMessage = "ADD LOCATION";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: '' },
                        { id: 'location', label: 'Storage Location', type: 'textfield', value: '' }
                    ];
                    displayLocationsAddEditDialog(titleMessage, fields);
                }
                else if (operation == 'EDIT')
                {
                    titleMessage = "EDIT LOCATION";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: row.id },
                        { id: 'location', label: 'Storage Location', type: 'textfield', value: row.location }
                    ];
                    displayLocationsAddEditDialog(titleMessage, fields);
                }
                else
                {
                    var pKey = row.id;
                    displayLocationsDeleteDialog("Are you sure you want to delete the selected storage location?", pKey);
                }
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayLocationsAddEditDialog(providedTitle, providedFields)
{
    if (locationsAddEditDialog != null)
    {
        locationsAddEditDialog.title = providedTitle;
        locationsAddEditDialog.fields = providedFields;
        locationsAddEditDialog.visible = true;
        return;
    }

    locationsAddEditDialog = new Vue
    ({
        el: '#locationsAddEditDialog',
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
                var locationID = this.fields[0].value;
                var location   = this.fields[1].value;

                if (typeof location === 'undefined' || location == null || location.trim().length == 0)
                    displayChemBaseNotificationDialog("Enter location!");
                else
                {
                    if (this.title == 'ADD LOCATION')
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendAddStorageLocationRequest(location);
                    }
                    else
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendEditStorageLocationRequest(locationID, location);                
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

function displayLocationsDeleteDialog(providedMessage, pKey)
{
    if (locationsDeleteDialog != null)
    {
        locationsDeleteDialog.message = providedMessage;
        locationsDeleteDialog.primaryKey = pKey;
        locationsDeleteDialog.visible = true;
        return;
    }

    locationsDeleteDialog = new Vue
    ({
        el: '#locationsDeleteDialog',
        data:
        {
            visible: true,
            title: "DELETE LOCATION",
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
                sendDeleteStorageLocationRequest(this.primaryKey);
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

function sendDeleteStorageLocationRequest(locationID)
{
    var data = JSON.stringify({"id": locationID});
    request.open('POST', BASE_URL + '/ChemBase/v1/locations/remove', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processDeleteStorageLocationResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processDeleteStorageLocationResponse()
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            locationsTable.rows = responseData['locations'];
            chemicalsSearchForm.fields[5].selectvalues = responseData['selectableLocations'];           
            sessionStorage.setItem("selectableLocations", JSON.stringify(responseData['selectableLocations']));
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

function sendAddStorageLocationRequest(location)
{
    var data = JSON.stringify({"location": location});
    request.open('POST', BASE_URL + '/ChemBase/v1/locations/add', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processAddStorageLocationResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processAddStorageLocationResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            locationsTable.rows = responseData['locations'];
            chemicalsSearchForm.fields[5].selectvalues = responseData['selectableLocations'];           
            sessionStorage.setItem("selectableLocations", JSON.stringify(responseData['selectableLocations']));
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

function sendEditStorageLocationRequest(locationID, location)
{
    var data = JSON.stringify({"id": locationID, "location": location});
    request.open('POST', BASE_URL + '/ChemBase/v1/locations/edit', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processEditStorageLocationResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processEditStorageLocationResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            locationsTable.rows = responseData['locations'];
            chemicalsSearchForm.fields[5].selectvalues = responseData['selectableLocations'];           
            sessionStorage.setItem("selectableLocations", JSON.stringify(responseData['selectableLocations']));

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
