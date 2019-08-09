var ioc = {
    tmpFilePool: {
        type: 'org.nutz.filepool.NutFilePool',
        // 临时文件最大个数为 1000 个
        args: [{java: "$conf.get('temp.Path')"}, {java: "$conf.get('temp.total')"}]
    },
    uploadFileContext: {
        type: 'org.nutz.mvc.upload.UploadingContext',
        singleton: false,
        args: [{refer: 'tmpFilePool'}],
        fields: {
            // 是否忽略空文件, 默认为 false
            ignoreNull: false,
            // 单个文件最大尺寸(大约的值，单位为字节，即 1048576 为 1M)
            maxFileSize: {java: "$conf.get('attach.upload.maxFileSize')"},
            // 正则表达式匹配可以支持的文件名
            nameFilter: {java: "$conf.get('file.extensions')"}
        }
    },
    upload: {
        type: 'org.nutz.mvc.upload.UploadAdaptor',
        singleton: false,
        args: [{refer: 'uploadFileContext'}]
    }
};