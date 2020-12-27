
/* ------------------------------------------------------------------------------------------ */

function constructUsersTable()
{
    usersTable = new Vue
    ({
        el: '#usersTable',
        data:
        {
            visible: false,
            currentPage: 1,
            elementsPerPage: 20,
            showBottomBar: true,

            editButtonIcon: 'resources/edit.svg',
            deleteButtonIcon: 'resources/remove.svg',
            editButtonTitle: 'Unlock/Change Password',
            deleteButtonTitle: 'Delete',

            tableFilters: {},
            filterFields: [],

            // Titles contains a list of column titles for each column
            // -------------------------------------------------------
            titles:
            {
                id: "ID",
                username: "USERNAME",
                locked: "LOCKED",
                lockDate: "LOCK DATE"
            },

            // This list contains all the columns that will be displayed on screen
            // -------------------------------------------------------------------
            columns: ['username', 'locked', 'lockDate'],

            // Rows contains an array with data in form of objects and their properties
            // ------------------------------------------------------------------------
            rows: [],
        },

        methods:
        {
            exportHandler: function(rows)
            {
                JSONToCSVConverter(rows, "Users", true);
            },

            popupHandler: function(operation, row)
            {
                var fields = null;          
                var titleMessage = null;

                if (operation == 'ADD')
                {
                    titleMessage = "ADD USER";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: '' },
                        { id: 'username', label: 'Username', type: 'textfield', value: '' },
                        { id: 'password', label: 'Enter password', type: 'password', value: '' },
                        { id: 'password_confirmation', label: 'Confirm password', type: 'password', value: '' }
                    ];
                    displayUsersAddEditDialog(titleMessage, fields);
                }
                else if (operation == 'EDIT')
                {
                    titleMessage = "CHANGE PASSWORD";
                    fields =
                    [
                        { id: 'id', label: 'ID', type: 'hidden', value: row.id },
                        { id: 'username', label: 'Username', type: 'textfield-noneditable', value: row.username },
                        { id: 'password', label: 'Enter password', type: 'password', value: '' },
                        { id: 'password_confirmation', label: 'Confirm password', type: 'password', value: '' }
                    ];
                    displayUsersAddEditDialog(titleMessage, fields);
                }
                else
                {
                    var pKey = row.id;
                    displayUsersDeleteDialog("Are you sure you want to delete the selected user?", pKey);
                }
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayUsersAddEditDialog(providedTitle, providedFields)
{
    if (usersAddEditDialog != null)
    {
        usersAddEditDialog.title = providedTitle;
        usersAddEditDialog.fields = providedFields;
        usersAddEditDialog.visible = true;
        return;
    }

    usersAddEditDialog = new Vue
    ({
        el: '#usersAddEditDialog',
        data:
        {
            visible: true,
            title: providedTitle,
            yesButtonMessage: "OK",
            noButtonMessage: "CANCEL",
            fields: providedFields,
        },

        methods:
        {
            yesButtonClickHandler: function()
            {
                var id                                  = this.fields[0].value;
                var username                            = this.fields[1].value;
                var password                            = this.fields[2].value;
                var passwordConfirmation                = this.fields[3].value;

                if (this.title == 'ADD USER')
                {
                    if (typeof username === 'undefined' || username == null || username.trim().length == 0)
                        displayChemBaseNotificationDialog("Enter username!");

                    else if (typeof password === 'undefined' || password == null || password.trim().length == 0)
                        displayChemBaseNotificationDialog("Enter password!");
                        
                    else if (password != passwordConfirmation)
                        displayChemBaseNotificationDialog("Passwords don't match!");

                    else
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendAddUserRequest(username, password);
                    }
                }
                else
                {
                    if (typeof password === 'undefined' || password == null || password.trim().length == 0)
                        displayChemBaseNotificationDialog("Enter password!");
                        
                    else if (password != passwordConfirmation)
                        displayChemBaseNotificationDialog("Passwords don't match!");

                    else
                    {
                        this.visible = false;
                        displayChemBaseProgressDialog();
                        sendChangePasswordRequest(id, password);
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

function displayUsersDeleteDialog(providedMessage, pKey)
{
    if (usersDeleteDialog != null)
    {
        usersDeleteDialog.message = providedMessage;
        usersDeleteDialog.primaryKey = pKey;
        usersDeleteDialog.visible = true;
        return;
    }

    usersDeleteDialog = new Vue
    ({
        el: '#usersDeleteDialog',
        data:
        {
            visible: true,
            title: "DELETE USER",
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
                sendDeleteUserRequest(this.primaryKey);
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

function sendDeleteUserRequest(userID)
{
    var data = JSON.stringify({"id": userID});
    request.open('POST', BASE_URL + '/ChemBase/v1/users/delete', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = function() { processDeleteUserResponse(userID) };
}

/* ------------------------------------------------------------------------------------------ */

function processDeleteUserResponse(userID)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;
        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            usersTable.rows = responseData['users'];                
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

function sendAddUserRequest(username, password)
{
    var data = JSON.stringify({"username": username, "password": password});
    request.open('POST', BASE_URL + '/ChemBase/v1/users/add', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processAddUserResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processAddUserResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            usersTable.rows = responseData['users'];
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

function sendChangePasswordRequest(userID, password)
{
    var data = JSON.stringify({"id": userID, "password": password});
    request.open('POST', BASE_URL + '/ChemBase/v1/users/edit', true);
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');
    request.send(data);

    request.onreadystatechange = processChangePasswordResponse;
}

/* ------------------------------------------------------------------------------------------ */

function processChangePasswordResponse(e)
{
    if (request.readyState == 4)
    {
        chemBaseProgressDialog.visible = false;

        if (request.status == 200)
        {
            var responseData = JSON.parse(request.responseText);
            usersTable.rows = responseData['users'];
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
