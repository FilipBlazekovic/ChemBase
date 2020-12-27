var BASE_URL = window.location.origin;
var request = new XMLHttpRequest();
var loginForm;
var progressDialog;
var notificationDialog;

/* ------------------------------------------------------------------------------------------ */

function initializeVue()
{
    initializeVueTemplates();
    constructChemBaseLoginForm();
}

function initializeVueTemplates()
{
    initializeChemBaseLoginForm();
    initializeChemBaseProgressDialog();
    initializeChemBaseNotificationDialog();
}

/* ------------------------------------------------------------------------------------------ */

function constructChemBaseLoginForm()
{
    loginForm = new Vue
    ({
        el: '#loginForm',
        data:
        {
            visible: true,
            title: "ChemBase",
            loginButtonMessage: "LOGIN"
        },
        methods:
        {
            loginButtonClickHandler: function(username, password)
            {
                if (typeof username === undefined || username == null || username.length <= 0)
                    displayNotificationDialog("Missing username!");
                else if (typeof password === undefined || password == null || password.length <= 0)
                    displayNotificationDialog("Missing password!");
                else
                {
                    displayProgressDialog();
                    sendLoginRequest(username, password);
                }
            }
        }
    });
}

/* ------------------------------------------------------------------------------------------ */

function displayProgressDialog()
{
    if (progressDialog != null)
    {
        progressDialog.visible = true;
        return;
    }

    progressDialog = new Vue
    ({
        el: '#progressDialog',
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

function displayNotificationDialog(providedMessage)
{
    if (notificationDialog != null)
    {
        notificationDialog.message = providedMessage;
        notificationDialog.visible = true;
        return;
    }

    notificationDialog = new Vue
    ({
        el: '#notificationDialog',
        data:
        {
            visible: true,
            title: "ERROR",
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

function sendLoginRequest(username, password)
{
    var data = JSON.stringify
    ({
        "username": username,
        "password": password
    });

    request.open('POST', BASE_URL + '/ChemBase/v1/login', true);
    
    request.setRequestHeader('Access-Control-Allow-Origin','*');
    request.setRequestHeader('Content-type','application/json');

    request.send(data);
    request.onreadystatechange = processLoginResponse;      
}

/* ------------------------------------------------------------------------------------------ */

function processLoginResponse(e)
{
    if (request.readyState == 4)
    {
        progressDialog.visible = false;
        
        if (request.status == 200)
        {
            window.location.href="main.html";
        }
        else if (request.status == 400)
        {
            var responseData = JSON.parse(request.responseText);
            if (typeof responseData !== 'undefined' && responseData['error'] !== 'undefined')
            {
                displayNotificationDialog(responseData['error']);
            }
            else
            {
                displayNotificationDialog("Something went wrong!");
            }
        }
        else
        {
            displayNotificationDialog("Something went wrong!");
        }
    }
}

/* ------------------------------------------------------------------------------------------ */
