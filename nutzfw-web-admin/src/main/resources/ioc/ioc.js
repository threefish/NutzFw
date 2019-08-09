var ioc = {
    sqlTplIocEventListener: {
        type: "com.github.threefish.nutz.sqltpl.SqlTplIocEventListener",
        args: [{refer: '$ioc'}]
    },
    beetlSqlTemplteEngineImpl: {
        type: "com.github.threefish.nutz.sqltpl.BeetlSqlTemplteEngineImpl",
        events: {
            create: "init"
        }
    },
    conf: {
        type: "org.nutz.ioc.impl.PropertiesProxy",
        fields: {
            keyIndex: 1,//可选(不可重复) 匹配 -Dnutz.conf.path.1=dev/huc/
            paths: ["custom/"],//上面的配置将替换我
        }
    },
    enumsConf: {
        type: "org.nutz.ioc.impl.PropertiesProxy",
        fields: {
            paths: ["enums/"],
        }
    },
    dataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {
            create: "init",
            depose: 'close'
        },
        fields: {
            url: {java: "$conf.get('db.url')"},
            username: {java: "$conf.get('db.username')"},
            password: {java: "$conf.get('db.password')"},
            testWhileIdle: true,
            validationQuery: {java: "$conf.get('db.validationQuery')"},
            maxActive: {java: "$conf.get('db.maxActive')"},
            filters: "config,wall,stat",
            connectionProperties: {java: "$conf.get('db.connectionProperties')"}
        }
    },
    noWalldataSource: {
        type: "com.alibaba.druid.pool.DruidDataSource",
        events: {
            create: "init",
            depose: 'close'
        },
        fields: {
            url: {java: "$conf.get('db.url')"},
            username: {java: "$conf.get('db.username')"},
            password: {java: "$conf.get('db.password')"},
            testWhileIdle: true,
            validationQuery: {java: "$conf.get('db.validationQuery')"},
            maxActive: {java: "$conf.get('db.maxActive')"},
            filters: "config,stat",
            connectionProperties: {java: "$conf.get('db.connectionProperties')"}
        }
    },
    dao: {
        type: "org.nutz.dao.impl.NutDao",
        args: [{refer: "dataSource"}]
    },
    noWallDao: {
        type: "org.nutz.dao.impl.NutDao",
        args: [{refer: "noWalldataSource"}]
    }
};