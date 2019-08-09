var ioc = {
    emailAuthenticator0: {
        type: "org.apache.commons.mail.DefaultAuthenticator",
        args: [{java: "$conf.get('mail.UserName0')"}, {java: "$conf.get('mail.Password')"}]
    },
    htmlEmail0: {
        type: "org.apache.commons.mail.ImageHtmlEmail",
        singleton: false,
        fields: {
            hostName: {java: "$conf.get('mail.HostName')"},
            smtpPort: {java: "$conf.get('mail.SmtpPort')"},
            authenticator: {refer: "emailAuthenticator0"},
            SSLOnConnect: {java: "$conf.get('mail.SSLOnConnect')"},
            from: {java: "$conf.get('mail.From0')"}
        }
    },
    emailAuthenticator1: {
        type: "org.apache.commons.mail.DefaultAuthenticator",
        args: [{java: "$conf.get('mail.UserName1')"}, {java: "$conf.get('mail.Password')"}]
    },
    htmlEmail1: {
        type: "org.apache.commons.mail.ImageHtmlEmail",
        singleton: false,
        fields: {
            hostName: {java: "$conf.get('mail.HostName')"},
            smtpPort: {java: "$conf.get('mail.SmtpPort')"},
            authenticator: {refer: "emailAuthenticator1"},
            SSLOnConnect: {java: "$conf.get('mail.SSLOnConnect')"},
            from: {java: "$conf.get('mail.From1')"}
        }
    },
    emailAuthenticator2: {
        type: "org.apache.commons.mail.DefaultAuthenticator",
        args: [{java: "$conf.get('mail.UserName2')"}, {java: "$conf.get('mail.Password')"}]
    },
    htmlEmail2: {
        type: "org.apache.commons.mail.ImageHtmlEmail",
        singleton: false,
        fields: {
            hostName: {java: "$conf.get('mail.HostName')"},
            smtpPort: {java: "$conf.get('mail.SmtpPort')"},
            authenticator: {refer: "emailAuthenticator2"},
            SSLOnConnect: {java: "$conf.get('mail.SSLOnConnect')"},
            from: {java: "$conf.get('mail.From2')"}
        }
    }
};