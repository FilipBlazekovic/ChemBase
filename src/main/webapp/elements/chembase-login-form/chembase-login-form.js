function initializeChemBaseLoginForm()
{

    Vue.component('chembase-login-form',
    {
        props: ['title',
                'loginbuttonmessage',
                'loginbuttonclickhandler'],
        
        data: function()
        {
            return { username: '', password: '' }
        },
                
        template: `

            <div class="chembase-login-form">
                <div class="chembase-login-form-content">
                    <div class="chembase-login-form-header">{{title}}</div>
                    <div class="chembase-login-form-body">

                        <div class="chembase-login-form-row">
                            <span class="chembase-login-form-label">Username: </span>
                        </div>

                        <div class="chembase-login-form-row">
                            <span><input type="text" v-model="username" v-on:keyup.enter="loginbuttonclickhandler(username, password)"></span>
                        </div>

                        <div class="chembase-login-form-row">
                            <span class="chembase-login-form-label">Password: </span>
                        </div>

                        <div class="chembase-login-form-row">
                            <input type="password" v-model="password" v-on:keyup.enter="loginbuttonclickhandler(username, password)">
                        </div>

                    </div>
                    <button class="chembase-login-form-button" v-on:click="loginbuttonclickhandler(username, password)">{{loginbuttonmessage}}</button>
                </div>
            </div>
        `
    });
}