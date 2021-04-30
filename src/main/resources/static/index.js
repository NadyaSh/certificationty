window.$ = window.jQuery = require("jquery");
$(function () {
    const abar = require('address_bar');
    const folder_view = require('folder_view');
    const fs = require('fs');
    const xml2js = require('xml2js');
    const jade = require('jade');
    const d = require('diskinfo');
    const {remote} = require('electron');
    const {shell} = remote;
    const mainProc = remote.require('./main');
    window.setSettings = mainProc.setSettings;

    DevExpress.localization.locale('ru');

    $("#popup").hide();
    $(".list-cont").hide();
    $("#wgtHidden").hide();
    $("#saveButton").hide();
    $("#showButton").hide();
    $("#addUser").hide();
    $("#tableButton").hide();
    $("#printTableButton").hide();
    $("#printButton").hide();

    let ResFlag = false;
    let dirpath = "";
    var userName = $("#user").text();
    var permitWriting = $("#role").text();
    var permitAll = $("#superrole").text();
    var formData;
    var ruleIsValid = true;
    var ruleIsValidH = true;
    var ruleIsValidA = true;
    var ruleIsValidO = true;
    var ruleIsValidK = true;
    var table;
    // var jsonContent = "";

    var gen_diskbar = jade.compile([
        '- each item, i in sequence',
        '    li(data-path="#{item.path}")',
        '      a(href="#") #{item.name}',
    ].join('\n'));

    $("#logout").dxButton({
        text: "Выйти ( " + userName + " )",
        type: "default",
        icon: "runner",
        useSubmitBehavior: true,
    });

    $("#addUser").dxButton({
        text: 'Добавить пользователя',
        type: "default",
        icon: "add",
        onClick: function () {
            window.open("/adduser");
        }
    });

    $("#chooseDir").dxButton({
        text: 'Выберите папку',
        type: "default",
        icon: "find",
        onClick: function () {
            popup.show();
        }
    });

    $("#saveButton").dxButton({
        text: 'Сохранить',
        type: "success",
        icon: "save",
        onClick: function () {
            $('#backgroundlayer').show();
            $('#saveButton').hide();
            mainProc.setSavePath(dirpath);
            if (ruleIsValid && ruleIsValidH && ruleIsValidA && ruleIsValidO && ruleIsValidK) {
                if (permitWriting === "true" || permitAll === "true")
                    addToFormData(wdg5List);
                if (formData.applicationNumber !== "" && formData.region.length >= 6) {
                    setFormData(wdg1List);
                    if (ResFlag) {
                        setFormData(wdg2ListR);
                        setFormData(wdg3ListR);
                        setFormData(wdg4ListR);
                    } else {
                        setFormData(wdg2ListK);
                        setFormData(wdg3ListK);
                        setFormData(wdg4ListK);
                    }
                    // let filename = formData.filesPath + "\\" + formData.metaData[0].data.slice(0, formData.metaData[0].data.length - 3) + "json";
                    // fs.writeFileSync(filename, JSON.stringify(formData), "utf-8");
                    $.ajax({
                        url: "/passport/save",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                        async: false
                    }).done(function (e) {
                        $("#aclick").html('<a id=\"clickmeSave\" href=\"/passport/report/' + e + '\" download=\"passport.pdf\"></a>');
                        setTimeout(function () {
                            document.getElementById('clickmeSave').click();
                        }, 50);
                    })
                        .fail(function (e) {
                            DevExpress.ui.notify({
                                message: "Произошла ошибка :(",
                                position: {my: "center top", at: "center top"}
                            }, "error", 3000);
                            z
                        });
                } else {
                    $('#saveButton').show();
                    DevExpress.ui.notify({
                        message: "Заполните поля \"Район\" (хотя бы 6 символов) и \"Заявка\"",
                        position: {my: "center top", at: "center top"}
                    }, "error", 3000);
                }
            } else {
                $('#saveButton').show();
                DevExpress.ui.notify({
                    message: "Продукт не может быть сертифицирован!",
                    position: {my: "center top", at: "center top"}
                }, "error", 3000);
            }
            $('#backgroundlayer').hide();
        }
    });

    $("#showButton").dxButton({
        text: 'Открыть паспорт',
        type: "success",
        icon: "doc",
        onClick: function () {
            window.open("file://" + formData.pdfpath, "_blank");
        }
    });

    $("#tableButton").dxButton({
        text: 'Отчет',
        type: "normal",
        icon: "bookmark",
        onClick: function () {
            $('#backgroundlayer').show();
            $("#printButton").hide();
            $("#printTableButton").show();
            var urlstr;
            var columns = [];
            if (permitWriting === "true" || permitAll === "true") {
                urlstr = "/passport/table";
                columns = [
                    {
                        "caption": "Номер заявки",
                        "dataField": "application"
                    },
                    {
                        "caption": "Номер задания",
                        "dataField": "task"
                    },
                    {
                        "caption": "Номер маршрута",
                        "dataField": "fileName"
                    },
                    {
                        "caption": "Дата поступления",
                        "dataField": "dateIn",
                        "dataType": "datetime",
                        "format": "yyyy.MM.dd HH:mm:ss"
                    },
                    {
                        "caption": "Дата сертификации",
                        "dataField": "dateCertification",
                        "dataType": "datetime",
                        "format": "yyyy.MM.dd HH:mm:ss"
                    },
                    {
                        "caption": "Аппарат",
                        "dataField": "vehicle"
                    }
                ];
            } else {
                urlstr = "/passport/tablexml";
                columns = [
                    {
                        "caption": "Номер маршрута",
                        "dataField": "fileName"
                    },
                    {
                        "caption": "Дата поступления",
                        "dataField": "dateIn",
                        "dataType": "datetime",
                        "format": "yyyy.MM.dd HH:mm:ss"
                    },
                    {
                        "caption": "Аппарат",
                        "dataField": "vehicle"
                    }
                ];
            }
            $.ajax({
                url: urlstr,
                type: "POST",
            }).done(function (e) {
                $('#backgroundlayer').hide();
                console.log(e);
                table = $("#table").dxDataGrid({
                    dataSource: e,
                    columnsAutoWidth: true,
                    wordWrapEnabled: true,
                    showBorders: true,
                    keyExpr: "id",
                    allowColumnResizing: true,
                    filterRow: {
                        visible: true,
                        applyFilter: "auto"
                    },
                    scrolling: {
                        mode: "virtual"
                    },
                    selection: {
                        mode: "single"
                    },
                    export: {
                        fileName: "Отчет о сертифицированных продуктах",
                    },
                    columns: columns,
                    onContextMenuPreparing: function (e) {
                        if (e.row.rowType === "data" && (permitWriting === "true" || permitAll === "true")) {
                            mainProc.setSavePath("");
                            e.component.selectRows([e.row.key], false);
                            e.items = [
                                {
                                    icon: "download",
                                    text: "Сохранить",
                                    onItemClick: function (a) {
                                        console.log(a);
                                        $("#aclick").html('<a id=\"clickmeTable\" href=\"/passport/report/' + e.row.key + '\" download=\"passport.pdf\"></a>');
                                        setTimeout(function () {
                                            document.getElementById('clickmeTable').click();
                                        }, 50);
                                    }
                                }]
                        }
                    }
                }).dxDataGrid("instance");
            })
                .fail(function (e) {
                    $('#backgroundlayer').hide();
                    DevExpress.ui.notify({
                        message: "Произошла ошибка :(",
                        position: {my: "center top", at: "center top"}
                    }, "error", 3000);
                });

            $("#tableCont").fadeIn();
            $("#wgtHidden").hide();
        }
    });

    $("#printTableButton").dxButton({
        text: 'Печать',
        type: "default",
        icon: "doc",
        onClick: function () {
            // mainProc.setSavePath("");
            $("#title").hide();
            $("#title3").show();
            let filter = table.columnOption("dateCertification", 'filterValue');
            let name = "Отчет о сертифицированных продуктах";
            document.styleSheets[0].href = "printtable.css";
            table.option("filterRow.visible", false);
            // table.option("columns[5].visible", false);
            if (filter) {
                let f1 = getFilterDate(filter[0]);
                let f2 = getFilterDate(filter[1]);
                name = "Отчет о сертифицированных продуктах c " + f1 + " по " + f2;
                table.option("export.fileName", name);
                $("#title3").text(name);
            }
            // table.exportToExcel();
            mainProc.printPassport(name, true);
            setTimeout(function () {
                table.option("filterRow.visible", true);
                // table.option("columns[5].visible", true);
                $("#title3").hide();
                $("#title").show();
                // mainProc.setSavePath(dirpath);
            }, 50);
        }
    });

    $("#printButton").dxButton({
        text: 'Печать',
        type: "default",
        icon: "doc",
        onClick: function () {
            $("#wgtHidden>div").show();
            $("#wgt5Div").hide();
            $("#title").hide();
            $("#title2").show();
            document.styleSheets[0].href = "print.css";
            mainProc.printPassport("Данные паспорта", false);
            setTimeout(function () {
                $("#title2").hide();
                $("#title").show();
                $("#wgtHidden>div").hide();
                $("#wgt1Div").show();
            }, 50);
        }
    });

    $("#menu").dxMenu({
        dataSource: menuList,
        itemTemplate: function (data, index) {
            let result = $("<div>").html(data.name);
            if (permitWriting === "false" && permitAll === "false" && data.id === 5)
                return;
            else
                return result;
        },
        selectionMode: "single",
        selectByClick: true,
        onItemClick: function (e) {
            $("#tableCont").hide();
            $("#wgtHidden").show();
            $("#printButton").show();
            $("#printTableButton").hide();
            $("#wgtHidden>div").hide();
            $("#" + e.itemData.div).fadeIn();
        }
    }).dxMenu("instance");

    var folder;
    var addressbar;

    var setFolderPath = function () {
        d.getDrives(function (err, aDrives) {
            var result = [];
            for (var i = 0; i < aDrives.length; i++) {
                result.push({
                    name: aDrives[i].mounted.replace(":", ""),
                    path: aDrives[i].mounted + "\\",
                });
            }
            $("#disks").html(gen_diskbar({sequence: result}));
            $("#disks a").click(function (e) {
                folder.open($(this).parent().attr('data-path'));
                addressbar.set($(this).parent().attr('data-path'));
            });
        });

        dirpath = localStorage.getItem("path");
        if (!dirpath)
            dirpath = process.cwd().split("\\")[0] + "\\";

        fs.readdir(dirpath, function (error, _) {
            if (error) {
                folder.open(process.cwd().split("\\")[0] + "\\");
                addressbar.set(process.cwd().split("\\")[0] + "\\");
            } else {
                folder.open(dirpath);
                addressbar.set(dirpath);
            }
        });
    };

    let popup = $("#popup").dxPopup({
        title: "Откройте директорию с продуктом",
        height: 690,
        visible: false,
        onContentReady: function () {
            $("#scrollview").dxScrollView({
                useNative: true
            });
            folder = new folder_view.Folder($('#files'));
            addressbar = new abar.AddressBar($('#addressbar'));

            folder.on('navigate', function (dir, mime) {
                if (mime.type === 'folder') {
                    dirpath = dir;
                    addressbar.enter(mime);
                } else {
                    shell.openItem(mime.path);
                }
            });

            addressbar.on('navigate', function (dir) {
                dirpath = dir;
                folder.open(dir);
            });

            folder.on('filesSize', function (files) {
                formData = {
                    filesPath: dirpath,
                    customer: "",
                    region: "",
                    applicationNumber: "",
                    boss: "",
                    controller: "",
                    operator: "",

                    fileName: [],
                    metaData: [],
                    quickLook: [],
                    shapeFiles: [],
                    geo: []

                };

                let customer = localStorage.getItem("customer");
                if (customer)
                    formData.customer = customer;
                let boss = localStorage.getItem("boss");
                if (boss)
                    formData.boss = boss;
                let controller = localStorage.getItem("controller");
                if (controller)
                    formData.controller = controller;
                let operator = localStorage.getItem("operator");
                if (operator)
                    formData.operator = operator;
                let applicationNumber = localStorage.getItem("applicationNumber");
                if (applicationNumber)
                    formData.applicationNumber = applicationNumber;
                let region = localStorage.getItem("region");
                if (region)
                    formData.region = region;

                localStorage.setItem("path", dirpath);
                $("#path").text(dirpath);

                for (let i = 0; i < files.length; i++) {
                    let ext = files[i].name.split(".")[1];
                    if (ext.toLowerCase() === "xml") {
                        var fileContent = fs.readFileSync(files[i].path, "UTF-8");
                        let item = {
                            data: files[i].name,
                            size: files[i].size,
                            content: fileContent.toString()
                        };
                        formData.metaData.push(item);
                    } else if (["ige", "img", "tiff", "tif"].indexOf(ext.toLowerCase()) !== -1) {
                        let item = {
                            label: "",
                            data: files[i].name,
                            size: files[i].size
                        };
                        formData.fileName.push(item);
                    } else if (["dbf", "prj", "shp", "shx"].indexOf(ext.toLowerCase()) !== -1) {
                        let item = {
                            data: files[i].name,
                            size: files[i].size
                        };
                        formData.shapeFiles.push(item)
                    } else if (ext.toLowerCase() === "twf") {
                        let item = {
                            data: files[i].name,
                            size: files[i].size
                        };
                        formData.geo.push(item)
                    } else if (ext.toLowerCase() === "jpg") {
                        let item = {
                            data: files[i].name,
                            size: files[i].size
                        };
                        formData.quickLook.push(item)
                        // } else if (ext === "json") {
                        //     jsonContent = fs.readFileSync(files[i].path, "UTF-8").toString();
                    } else if (ext.toLowerCase() === "pdf") {
                        formData.pdfpath = files[i].path;
                    }
                }

                if (formData.metaData.length > 0) {
                    popup.hide();
                    // let url = "";
                    // if (permitWriting === "true" || permitAll === "true") {
                    //     url = "/passport/parser";
                    $.ajax({
                        url: "/passport/parser",
                        type: "POST",
                        contentType: "application/json",
                        data: JSON.stringify(formData),
                    }).done(function (e) {
                        $('#backgroundlayer').hide();
                        console.log(e);
                        formData = e;
                        prepareInitWidgets();
                        if (formData.numberV.includes("KV") || formData.numberV.includes("BKA")) {
                            if (formData.alpha === "") {
                                ruleIsValidA = false;
                                DevExpress.ui.notify({
                                    message: "Не заполнен угол крена!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                            if (formData.omega === "") {
                                ruleIsValidO = false;
                                DevExpress.ui.notify({
                                    message: "Не заполнен угол тангажа!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                            if (formData.kappa === "") {
                                ruleIsValidK = false;
                                DevExpress.ui.notify({
                                    message: "Не заполнен угол рысканья!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                            if (formData.sunHeightDuring === "") {
                                ruleIsValidH = false;
                                DevExpress.ui.notify({
                                    message: "Не заполена высота Солнца!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                        } else {
                            if (formData.angleOptSys === "") {
                                ruleIsValidA = false;
                                DevExpress.ui.notify({
                                    message: "Не заполнен угол между местной вертикалью и осью визирования!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                            if (formData.sunHeightDuring === "") {
                                ruleIsValidH = false;
                                DevExpress.ui.notify({
                                    message: "Не заполена высота Солнца!",
                                    position: {my: "center top", at: "center top"}
                                }, "info", 6000);
                            }
                        }
                        if (!ruleIsValid || !ruleIsValidH || !ruleIsValidA || !ruleIsValidO || !ruleIsValidK) {
                            DevExpress.ui.notify({
                                message: "Продукт не может быть сертифицирован!",
                                position: {my: "center top", at: "center top"}
                            }, "error", 3000);
                        }
                    })
                        .fail(function (e) {
                            $('#backgroundlayer').hide();
                            DevExpress.ui.notify({
                                message: "Произошла ошибка :(",
                                position: {my: "center top", at: "center top"}
                            }, "error", 3000);
                        });
                    // } else {
                    //     $('#backgroundlayer').hide();
                    //     url = "/passport/read";
                    //     // formData = JSON.parse(jsonContent);
                    //     prepareInitWidgets();
                    // }
                } else {
                    $('#backgroundlayer').hide();
                    DevExpress.ui.notify({
                        message: "Откройте папку с комплектом продукта",
                        position: {my: "center top", at: "center top"}
                    }, "error", 3000);
                }
            });

            setFolderPath();
            $("#applyButton").dxButton({
                text: "Выбрать",
                type: "success",
                onClick: function () {
                    $('#backgroundlayer').show();
                    folder.getFilesSize(dirpath);
                }
            });
        }
    }).dxPopup("instance");

    if (permitAll === "true")
        $("#addUser").show();
    else
        popup.show();
    $("#tableButton").show();

    var prepareInitWidgets = function () {
        if (formData.typeV === "") {
            DevExpress.ui.notify({
                message: "Произошла ошибка :(",
                position: {my: "center top", at: "center top"}
            }, "error", 3000);
            return;
        }
        ruleIsValid = true;
        ruleIsValidH = true;
        ruleIsValidA = true;
        ruleIsValidO = true;
        ruleIsValidK = true;
        if (permitWriting === "true" || permitAll === "true") {
            $("#saveButton").fadeIn();
        } else {
            $("#showButton").fadeIn();
        }
        $("#tableCont").hide();
        $("#printTableButton").hide();
        $("#printButton").fadeIn();
        $("#wgtHidden").html('<div id=\"wgt1Div\" class=\"wgt-cont\"></div>' +
            '<div id=\"wgt2Div\" class=\"wgt-cont\"></div>' +
            '<div id=\"wgt3Div\" class=\"wgt-cont\"></div>' +
            '<div id=\"wgt4Div\" class=\"wgt-cont\"></div>' +
            '<div id=\"wgt5Div\">' +
            '    <div id=\"titleData\"></div>' +
            '</div>');

        let f = "success";
        ResFlag = !!formData.typeV.includes("Ресурс");
        if (formData.errormess.includes("Данные получены")) {
            $(".list-cont").fadeIn();
            $("#wgtHidden").fadeIn();
            initWidgets();
        } else {
            $(".list-cont").hide();
            $("#wgtHidden").hide();
            f = "error";
        }
        DevExpress.ui.notify({
            message: formData.errormess,
            position: {my: "center top", at: "center top"}
        }, f, 3000);
    };

    var initWidgets = function () {
        loadList(wdg1List.item1, formData.fileName, "fileName");
        loadList(wdg1List.item2, formData.metaData, "metaData");
        loadList(wdg1List.item3, formData.quickLook, "quickLook");
        loadList(wdg1List.item4, formData.shapeFiles, "shapeFiles");
        loadAngles();
        if (ResFlag) {
            loadRange(wdg3ListR.item7, formData.channelRange, "channelRange", "range");
            delete wdg1List.item5;
            createWidget(wdg2ListR, "#wgt2Div");
            createWidget(wdg3ListR, "#wgt3Div");
            createWidget(wdg4ListR, "#wgt4Div");
        } else {
            if (formData.geo[0].data !== "") {
                loadList(wdg1List.item5, formData.geo, "geo");
            }
            loadRange(wdg3ListK.item6, formData.channelRange, "channelRange", "range");
            createWidget(wdg2ListK, "#wgt2Div");
            createWidget(wdg3ListK, "#wgt3Div");
            createWidget(wdg4ListK, "#wgt4Div");
        }
        createWidget(wdg1List, "#wgt1Div");
        if (permitWriting === "true" || permitAll === "true")
            createWidget(wdg5List, "#titleData");
        $("#wgtHidden>div").hide();
        $("#wgt1Div").fadeIn();
    };

    var loadList = function (witem, field, parent) {
        witem.list = [];
        for (let i in field) {
            let item = {
                type: "str",
                label: field[i].label,
                wgt: null,
                data: "data",
                index: i,
                parent: parent,
                rule: witem.list.rule,
                mess: witem.list.mess
            };
            let itemsize = {
                type: "str",
                label: "размер, байт:",
                wgt: null,
                data: "size",
                index: i,
                parent: parent
            };
            witem.list.push(item);
            witem.list.push(itemsize);
        }
    };

    var loadRange = function (witem, field, parent, type) {
        witem.list = [];
        for (let i in field) {
            let item = {
                type: type,
                label: field[i].label,
                wgt: null,
                data: "data",
                index: i,
                parent: parent,
                rule: witem.rule,
                mess: witem.mess
            };
            witem.list.push(item);
        }
    };

    var loadAngles = function () {
        if (ResFlag) {
            wdg4ListR.item2.list = [];
            for (let i in formData.channelAngle) {
                let item = Object.assign({}, anglesR);
                item.label = formData.channelAngle[i].name;
                item.index = i;
                item.parent = "channelAngle";
                wdg4ListR.item2.list.push(item);
            }
        } else {
            wdg4ListK.item2.list = [];
            for (let i in formData.channelAngle) {
                let item = Object.assign({}, anglesK);
                item.label = formData.channelAngle[i].name;
                item.index = i;
                item.parent = "channelAngle";
                wdg4ListK.item2.list.push(item);
            }
        }
    };

    var createLable = function (widget) {
        let lableDiv;
        if (widget.label !== "")
            lableDiv = $("<div>", {
                text: widget.label,
                class: "label"
            });
        return lableDiv;
    };

    var createTextBox = function (item, inputDiv, data) {
        let readOnly = true;
        if (data === "" || item.type === "edit")
            readOnly = false;
        let wgt = inputDiv.dxTextBox({
            isValid: true,
            readOnly: readOnly,
            maskInvalidMessage: "Неправильное значение",
            onFocusOut: function (e) {
                checkRule(item, item.wgt.option("value"));
                if (item.data.includes("Lat"))
                    checkLat(item, item.wgt.option("value"));
                if (item.data.includes("Long"))
                    checkLong(item, item.wgt.option("value"));
            }
        }).dxTextBox("instance");
        wgt.option("value", data === undefined ? "" : data);
        return wgt;
    };

    var createNumberBox = function (item, inputDiv, data) {
        let readOnly = true;
        if (data === "")
            readOnly = false;
        let wgt = inputDiv.dxNumberBox({
            isValid: true,
            readOnly: readOnly,
            onFocusOut: function (e) {
                checkRule(item, item.wgt.option("value"));
            }
        }).dxNumberBox("instance");
        wgt.option("value", data === undefined ? "" : data);
        return wgt;
    };

    var createList = function (list, outsideDiv) {
        for (let j in list) {
            if (list[j].label !== "") {
                let inDiv = $("<div>");
                createLable(list[j]).appendTo($(inDiv));
                inDiv.appendTo($(outsideDiv));
            }
            let inputDiv = $("<div>");
            list[j].wgt = createTextBox(list[j], inputDiv, formData[list[j].parent][list[j].index][list[j].data]);
            inputDiv.appendTo($(outsideDiv));
        }
    };

    var createMultilist = function (list, outsideDiv) {
        for (let j in list) {
            if (list[j].label !== "")
                createLable(list[j]).appendTo($(outsideDiv));
            for (let l in list[j].list) {
                let inputDiv = $("<div>", {
                    class: "couple"
                });
                createLable(list[j].list[l]).appendTo($(inputDiv));
                let div2 = $("<div>");
                list[j].list[l].wgt = createTextBox(list[j].list[l], div2, formData[list[j].parent][list[j].index][list[j].list[l].data]);
                div2.appendTo($(inputDiv));
                inputDiv.appendTo($(outsideDiv));
                if (list[j].list[l].data.includes("Lat"))
                    checkLat(list[j].list[l], formData[list[j].parent][list[j].index][list[j].list[l].data]);
                if (list[j].list[l].data.includes("Long"))
                    checkLong(list[j].list[l], formData[list[j].parent][list[j].index][list[j].list[l].data]);
            }

        }
    };

    var checkRule = function (item, data) {
        if (data == null)
            return;
        if (item.rule) {
            let flag = true;
            item.wgt.option("hint", "");
            if (item.type === "sunheight") {
                if (data.includes(":")) {
                    let list = data.split(":");
                    let sec = list[2] / 60;
                    let min = (parseFloat(list[1]) + sec) / 60;
                    data = parseFloat(list[0]) + min;
                }
                if (data <= parseFloat(item.rule)) {
                    ruleIsValidH = false;
                    flag = false;
                }
            } else if (item.type === "angleOpt") {
                if (data.includes(":")) {
                    let list = data.split(":");
                    let sec = list[2] / 60;
                    let min = (parseFloat(list[1]) + sec) / 60;
                    data = parseFloat(list[0]) + min;
                }
                if (data >= parseFloat(formData.maxAngle)) {
                    flag = false;
                    ruleIsValidA = false;
                }
            } else if (item.data === "alpha") {
                if (parseFloat(data) >= parseFloat(formData.maxAngle)) {
                    flag = false;
                    ruleIsValidA = false;
                }
            } else if (item.data === "omega") {
                if (parseFloat(data) >= parseFloat(formData.maxAngle)) {
                    flag = false;
                    ruleIsValidO = false;
                }
            } else if (item.data === "kappa") {
                if (parseFloat(data) >= parseFloat(formData.maxAngle)) {
                    flag = false;
                    ruleIsValidK = false;
                }
            }
            if (flag === false)
                item.wgt.option("hint", item.mess);

            item.wgt.option("isValid", flag);
        }
    };

    var checkLat = function (item, data) {
        if (data == null)
            return;
        let flag = true;
        if (data.includes(":")) {
            let list = data.split(":");
            let sec = list[2] / 60;
            let min = (parseFloat(list[1]) + sec) / 60;
            data = parseFloat(list[0]) + min;
        }
        if (-90.0 > data && data > 90.0) {
            flag = false;
            ruleIsValid = false;
        }
        item.wgt.option("isValid", flag);
    };

    var checkLong = function (item, data) {
        if (data == null)
            return;
        let flag = true;
        if (data.includes(":")) {
            let list = data.split(":");
            let sec = list[2] / 60;
            let min = (parseFloat(list[1]) + sec) / 60;
            data = parseFloat(list[0]) + min;
        }
        if (-180.0 > data && data > 180.0) {
            flag = false;
            ruleIsValid = false;
        }
        item.wgt.option("isValid", flag);
    };

    var createWidget = function (widgetList, widgetDiv) {
        for (let i in widgetList) {
            let outsideDiv = $("<div>", {
                class: "couple"
            });
            outsideDiv.appendTo($(widgetDiv));
            if (["str", "sunheight", "angleOpt"].indexOf(widgetList[i].type) !== -1) {
                createLable(widgetList[i]).appendTo($(outsideDiv));
                let inputDiv = $("<div>");
                widgetList[i].wgt = createTextBox(widgetList[i], inputDiv, formData[widgetList[i].data]);
                if (["sunheight", "angleOpt"].indexOf(widgetList[i].type) !== -1)
                    checkRule(widgetList[i], formData[widgetList[i].data]);
                inputDiv.appendTo($(outsideDiv));
            } else if (widgetList[i].type === "list") {
                if (widgetList[i].list.length > 0) {
                    createLable(widgetList[i]).appendTo($(outsideDiv));
                    createList(widgetList[i].list, outsideDiv);
                }
            } else if (widgetList[i].type === "multilist") {
                createLable(widgetList[i]).appendTo($(outsideDiv));
                createMultilist(widgetList[i].list, $(widgetDiv));
            } else if (["num"].indexOf(widgetList[i].type) !== -1) {
                createLable(widgetList[i]).appendTo($(outsideDiv));
                let inputDiv = $("<div>");
                widgetList[i].wgt = createNumberBox(widgetList[i], inputDiv, formData[widgetList[i].data]);
                if (["alpha", "omega", "kappa"].indexOf(widgetList[i].data) !== -1)
                    checkRule(widgetList[i], formData[widgetList[i].data]);
                inputDiv.appendTo($(outsideDiv));
            } else if (widgetList[i].type === "") {
                createLable(widgetList[i]).appendTo($(outsideDiv));
            } else if (widgetList[i].type === "edit") {
                createLable(widgetList[i]).appendTo($(outsideDiv));
                let inputDiv = $("<div>");
                widgetList[i].wgt = createTextBox(widgetList[i], inputDiv, formData[widgetList[i].data]);
                inputDiv.appendTo($(outsideDiv));
            }
        }
    };

    var setFormData = function (wdgList) {
        for (let i in wdgList) {
            if (["str", "num", "sunheight", "angleOpt"].indexOf(wdgList[i].type) !== -1) {
                if (wdgList[i].wgt)
                    formData[wdgList[i].data] = String(wdgList[i].wgt.option("value"));
            } else if (wdgList[i].type === "list") {
                for (let j in wdgList[i].list) {
                    if (wdgList[i].list[j].wgt)
                        formData[wdgList[i].list[j].parent][wdgList[i].list[j].index][wdgList[i].list[j].data] = wdgList[i].list[j].wgt.option("value");
                }
            } else if (wdgList[i].type === "multilist") {
                for (let j in wdgList[i].list) {
                    for (let k in wdgList[i].list[j].list) {
                        if (wdgList[i].list[j].list[k].wgt)
                            formData[wdgList[i].list[j].parent][wdgList[i].list[j].index][wdgList[i].list[j].list[k].data] = wdgList[i].list[j].list[k].wgt.option("value");
                    }
                }
            }
        }
    };

    var addToFormData = function (wdgList) {
        for (let i in wdgList) {
            if (wdgList[i].type === "edit") {
                if (wdgList[i].wgt) {
                    formData[wdgList[i].data] = String(wdgList[i].wgt.option("value"));
                    localStorage.setItem(wdgList[i].data, String(wdgList[i].wgt.option("value")));
                }
            }
        }
    };

    var getFilterDate = function (date) {
        let shotYear = date.getFullYear();
        let shotMonth = date.getMonth() + 1;
        let shotDay = date.getDate();
        if (shotMonth < 10) shotMonth = "0" + shotMonth;
        if (shotDay < 10) shotDay = "0" + shotDay;
        return shotDay + "." + shotMonth + "." + shotYear;
    }
});