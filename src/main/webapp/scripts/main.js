var BASE_URL = window.location.origin;

var request = new XMLHttpRequest();

var chemBaseNavBar;
var chemBaseNotificationDialog;
var chemBaseProgressDialog;

var chemicalsSearchForm;
var chemicalsTable;
var chemicalsAddEditDialog;
var chemicalsDeleteDialog;

var locationsTable;
var locationsAddEditDialog;
var locationsDeleteDialog;

var usersTable;
var usersAddEditDialog;
var usersDeleteDialog;

/* ------------------------------------------------------------------------------------------ */

function initializeVue()
{
    /* Disables direct access to this page and
     * forces users to go through login screen.
     * This is a simple hack given that referer can be spoofed.
     * Even if access is gained, no data is retrieved from the server.
     * Real authentication is based on sessionID on the server side.
     */
    var currentReferer = document.referrer;
    if (typeof currentReferer === 'undefined' || currentReferer == null || currentReferer.includes("ChemBase") == false)
        window.location.replace("index.html");

    initializeVueTemplates();
    constructChemBaseNavbar();
    constructChemicalsSearchForm();
    constructChemicalsTable();
    constructLocationsTable();
    constructUsersTable();
    fetchDataFromServer();
}

function initializeVueTemplates()
{
    initializeChemBaseNavbar();
    initializeChemBaseSearchForm();
    initializeChemBaseTable();
    initializeChemBaseConfirmationDialog();
    initializeChemBaseNotificationDialog();
    initializeChemBaseProgressDialog();
    initializeChemBaseOverlayForm();
    initializeChemBaseOverlayFormSimple();
}

/* ------------------------------------------------------------------------------------------ */

function constructChemBaseNavbar()
{
    chemBaseNavbar = new Vue
    ({
        el: '#chemBaseNavbar',
        data:
        {
            visible: true,
            buttons:
            [
                { id: "chemicals",  active: true,   visible: true,  text: "Chemicals" },
                { id: "locations",  active: false,  visible: true,  text: "Storage Locations" },
                { id: "users",      active: false,  visible: false, text: "Users" },
                { id: "logout",     active: false,  visible: true,  text: "Logout" }
            ]
        },
        
        methods:
        {
            buttonHandler: function(buttonID)
            {
                if (buttonID === 'chemicals')
                {
                    this.buttons[0].active      = true;
                    this.buttons[1].active      = false;
                    this.buttons[2].active      = false;
                    this.buttons[3].active      = false;

                    locationsTable.visible      = false;
                    usersTable.visible          = false;
                    chemicalsTable.visible      = true;
                    chemicalsSearchForm.visible = true;
                }
                else if (buttonID === 'locations')
                {
                    this.buttons[0].active      = false;
                    this.buttons[1].active      = true;
                    this.buttons[2].active      = false;
                    this.buttons[3].active      = false;
                
                    usersTable.visible          = false;
                    chemicalsTable.visible      = false;
                    chemicalsSearchForm.visible = false;
                    locationsTable.visible      = true;
                }
                else if (buttonID === 'users')
                {   
                    this.buttons[0].active      = false;
                    this.buttons[1].active      = false;
                    this.buttons[2].active      = true;
                    this.buttons[3].active      = false;
                
                    locationsTable.visible      = false;
                    chemicalsTable.visible      = false;
                    chemicalsSearchForm.visible = false;
                    usersTable.visible          = true;
                }
                else
                {                   
                    sendLogoutRequest();
                }
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayChemBaseProgressDialog()
{
    if (chemBaseProgressDialog != null)
    {
        chemBaseProgressDialog.visible = true;
        return;
    }

    chemBaseProgressDialog = new Vue
    ({
        el: '#chemBaseProgressDialog',
        data:
        {
            visible: true,
            title: "SENDING REQUEST",
            buttonMessage: "OK"
        },
        methods:
        {
            buttonClickHandler: function()
            {
                this.visible = false;
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayChemBaseNotificationDialog(providedMessage)
{
    if (chemBaseNotificationDialog != null)
    {
        chemBaseNotificationDialog.message = providedMessage;
        chemBaseNotificationDialog.visible = true;
        return;
    }

    chemBaseNotificationDialog = new Vue
    ({
        el: '#chemBaseNotificationDialog',
        data:
        {
            visible: true,
            title: "NOTIFICATION",
            message: providedMessage,
            buttonMessage: "OK"
        },
        methods:
        {
            buttonClickHandler: function()
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

function fetchDataFromServer()
{
    request.open('GET', BASE_URL + '/ChemBase/v1/init', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send();
    request.onreadystatechange = processFetchResponse;
}

function processFetchResponse(e)
{
    if (request.readyState == 4)
    {
        if (request.status == 200)
        {
            var responseData    = JSON.parse(request.responseText);
            usersTable.rows     = responseData['users'];
            locationsTable.rows = responseData['locations'];
            chemicalsTable.rows = responseData['chemicals'];
            chemicalsSearchForm.fields[5].selectvalues = responseData['selectableLocations'];
            sessionStorage.setItem("selectableLocations", JSON.stringify(responseData['selectableLocations']));
            sessionStorage.setItem("adminUser", responseData['adminUser']);
            
            if (responseData['adminUser'] == 1)
            {
                chemBaseNavbar.buttons[2].visible = true;
            }
        }
        else
        {
            console.log("[ERROR] Could not retrieve data from the server!");
            window.location.href="index.html"
        }
    }
}

/* ------------------------------------------------------------------------------------------ */

function sendLogoutRequest()
{
    request.open('GET', BASE_URL + '/ChemBase/v1/logout', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send();    
    request.onreadystatechange = processLogoutResponse;
}

function processLogoutResponse(e)
{
    if (request.readyState == 4) { window.location.href="index.html" }
}

/* ------------------------------------------------------------------------------------------ */
