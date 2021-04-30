var menuList = [{
    "id": 1,
    "name": 'Комплектность продукта',
    "div": "wgt1Div"
}, {
    "id": 2,
    "name": 'Постоянные параметры продукта',
    "div": "wgt2Div"
}, {
    "id": 3,
    "name": 'Переменные параметры продукта',
    "div": "wgt3Div"
}, {
    "id": 4,
    "name": 'Переменные параметры продукта (продолжение)',
    "div": "wgt4Div"
}, {
    "id": 5,
    "name": 'Дополнительные поля',
    "div": "wgt5Div"
}];

var wdg5List = {
    item1: {
        type: "edit",
        label: "Уровень обработки:",
        data: "level",
        wgt: null
    },
    item2: {
        type: "edit",
        label: "Заказчик:",
        data: "customer",
        wgt: null
    },
    item3: {
        type: "edit",
        label: "Район:",
        data: "region",
        wgt: null
    },
    item4: {
        type: "edit",
        label: "Заявка №:",
        data: "applicationNumber",
        wgt: null
    },
    item5: {
        type: "edit",
        label: "Начальник отдела:",
        data: "boss",
        wgt: null
    },
    item6: {
        type: "edit",
        label: "Контролер:",
        data: "controller",
        wgt: null
    },
    item7: {
        type: "edit",
        label: "Оператор:",
        data: "operator",
        wgt: null
    }
}

var wdg1List = {
    item1: {
        type: "list",
        label: "1. Изображение (имя файла):",
        list: []
    },
    item2: {
        type: "list",
        label: "2. Метаданные:",
        list: []
    },
    item3: {
        type: "list",
        label: "3. Квиклук:",
        list: []
    },
    item4: {
        type: "list",
        label: "4. Шейп-файлы:",
        list: []
    },
    item5: {
        type: "list",
        label: "5. Данные геопривязки:",
        list: []
    }
}

var wdg2ListR = {
    item1: {
        type: "str",
        label: "1. Тип КА",
        wgt: null,
        data: "typeV"
    },
    item2: {
        type: "str",
        label: "2. Тип целевой аппаратуры",
        wgt: null,
        data: "equipment"
    },
    item3: {
        type: "str",
        label: "3. Размер пиксела приемника изображения, мкм",
        wgt: null,
        data: "pixelSize"
    },
    item4: {
        type: "str",
        label: "4. Спектральные диапазоны",
        wgt: null,
        data: "range"
    },
    item5: {
        type: "str",
        label: "5. Высота Солнца над плоскостью местного горизонта, град",
        wgt: null,
        data: "sunHeight"
    },
    item6: {
        type: "str",
        label: "6. Максимальное значение угла отклонения оси визирования по крену, град",
        wgt: null,
        data: "maxAngle"
    },
    item7: {
        type: "str",
        label: "7. Вид сжатия",
        wgt: null,
        data: "compressionType"
    },
    item8: {
        type: "str",
        label: "8. Степень сжатия",
        wgt: null,
        data: "compressionRate"
    },
    item9: {
        type: "str",
        label: "9. Точность геодезической привязки изображения (СКО), м",
        wgt: null,
        data: "alignAccuracy"
    }
}

var wdg2ListK = {
    item1: {
        type: "str",
        label: "1. Тип КА",
        wgt: null,
        data: "typeV"
    },
    item2: {
        type: "str",
        label: "2. Тип целевой аппаратуры",
        wgt: null,
        data: "equipment"
    },
    item3: {
        type: "str",
        label: "3. Размер пиксела приемника изображения, мкм",
        wgt: null,
        data: "pixelSize"
    },
    item4: {
        type: "str",
        label: "4. Спектральные диапазоны",
        wgt: null,
        data: "range"
    },
    item5: {
        type: "str",
        label: "5. Радиометрическое разрешение, бит",
        wgt: null,
        data: "resolution"
    },
    item6: {
        type: "str",
        label: "6. Высота Солнца над плоскостью местного горизонта, град",
        wgt: null,
        data: "sunHeight"
    },
    item7: {
        type: "str",
        label: "7. Максимальное значение угла отклонения оси визирования по крену, град",
        wgt: null,
        data: "maxAngle"
    },
    item8: {
        type: "str",
        label: "8. Точность геодезической привязки изображения (СКО), м",
        wgt: null,
        data: "alignAccuracy"
    }
}

var wdg3ListR = {
    item1: {
        type: "str",
        label: "1. Номер КА",
        wgt: null,
        data: "numberV"
    },
    item2: {
        type: "num",
        label: "2. Номер витка",
        wgt: null,
        data: "numberCoil"
    },
    item3: {
        type: "num",
        label: "3. Номер включения на витке",
        wgt: null,
        data: "numberTurnOn"
    },
    item4: {
        type: "str",
        label: "4. Дата съемки, день.месяц.год",
        wgt: null,
        data: "sceneDate"
    },
    item5: {
        type: "str",
        label: "5. Московское время начала съемки",
        wgt: null,
        data: "sceneTime"
    },
    item6: {
        type: "num",
        label: "6. Продолжительность съемки, c",
        wgt: null,
        data: "deltaTime"
    },
    item7: {
        type: "list",
        label: "7. Границы длин волн спектральных диапазонов, нм",
        list: [],
        data: "channelRange"
    },
    item8: {
        type: "str",
        label: "8. Радиометрическое разрешение, бит",
        wgt: null,
        data: "resolution"
    },
    item9: {
        type: "str",
        label: "9. Формат файла изображения",
        wgt: null,
        data: "format"
    },
    item10: {
        type: "num",
        label: "10. Размер стороны проекции пиксела на земной поверхности с точностью не хуже 0,1 м",
        wgt: null,
        data: "sideSize"
    },
    item11: {
        type: "angleOpt",
        label: "11. Угол между местной вертикалью и осью визирования оптической системы, град:мин:сек",
        wgt: null,
        data: "angleOptSys",
        rule: "**.*",
        mess: "Не соответствует формату \"**.*\" или больше максимального угла"
    },
    item12: {
        type: "sunheight",
        label: "12. Высота Солнца в ходе съемки, град:мин:сек",
        wgt: null,
        data: "sunHeightDuring",
        rule: "10",
        mess: "Не соответствует формату \"**\" или менее 10 град"
    }
}

var wdg3ListK = {
    item1: {
        type: "str",
        label: "1. Номер КА",
        wgt: null,
        data: "numberV"
    },
    item2: {
        type: "num",
        label: "2. Номер витка",
        wgt: null,
        data: "numberCoil"
    },
    item3: {
        type: "num",
        label: "3. Номер включения на витке",
        wgt: null,
        data: "numberTurnOn"
    },
    item4: {
        type: "str",
        label: "4. Дата съемки, день.месяц.год",
        wgt: null,
        data: "sceneDate"
    },
    item5: {
        type: "str",
        label: "5. Московское время начала съемки",
        wgt: null,
        data: "sceneTime"
    },
    item6: {
        type: "list",
        label: "6. Границы длин волн спектральных диапазонов, нм",
        list: [],
        data: "channelRange"
    },
    item7: {
        type: "str",
        label: "7. Формат файла изображения",
        wgt: null,
        data: "format"
    },
    item8: {
        type: "",
        label: "8. Отклонение КА по углам:",
        wgt: null,
    },
    item9: {
        type: "num",
        label: "крена, град",
        wgt: null,
        rule: "40",
        data: "alpha"
    },
    item10: {
        type: "num",
        label: "тангажа, град",
        wgt: null,
        rule: "40",
        data: "omega"
    },
    item11: {
        type: "num",
        label: "рысканья, град",
        wgt: null,
        rule: "40",
        data: "kappa"
    },
    item12: {
        type: "sunheight",
        label: "9. Высота Солнца в ходе съемки, град",
        wgt: null,
        data: "sunHeightDuring",
        rule: "10",
        mess: "Не соответствует формату \"**\" или менее 10 град"
    }
}

var wdg4ListR = {
    item1: {
        type: "str",
        label: "13. Система координат",
        wgt: null,
        data: "coordinateSystem"
    },
    item2: {
        type: "multilist",
        label: "14. Координаты углов снимка в системе координат WGS - 84",
        list: []
    }
}

var wdg4ListK = {
    item1: {
        type: "str",
        label: "10. Система координат",
        wgt: null,
        data: "coordinateSystem"
    },
    item2: {
        type: "multilist",
        label: "11. Координаты углов снимка в системе координат WGS - 84",
        list: []
    }
}

var anglesR = {
    label: "",
    index: "",
    parent: "",
    list: [{
        type: "degree",
        label: "а. Геодезическая широта северо-западного угла, град.мин.сек",
        wgt: null,
        data: "anwlat"

    },
        {
            type: "degree",
            label: "б. Геодезическая долгота северо-западного угла, град.мин.сек",
            wgt: null,
            data: "anwlong"
        },
        {
            type: "degree",
            label: "в. Геодезическая широта северо-восточного угла, град.мин.сек",
            wgt: null,
            data: "anelat"
        },
        {
            type: "degree",
            label: "г. Геодезическая долгота северо-восточного угла, град.мин.сек",
            wgt: null,
            data: "anelong"
        },
        {
            type: "degree",
            label: "д. Геодезическая широта юго-восточного угла, град.мин.сек",
            wgt: null,
            data: "aselat"
        },
        {
            type: "degree",
            label: "е. Геодезическая долгота юго-восточного угла, град.мин.сек",
            wgt: null,
            data: "aselong"
        },
        {
            type: "degree",
            label: "ж. Геодезическая широта юго-западного угла, град.мин.сек",
            wgt: null,
            data: "aswlat"
        },
        {
            type: "degree",
            label: "з. Геодезическая долгота юго-западного угла, град.мин.сек",
            wgt: null,
            data: "aswlong"
        },
        {
            type: "degree",
            label: "и. Геодезическая широта центральной точки, град.мин.сек",
            wgt: null,
            data: "amidLat"
        },
        {
            type: "degree",
            label: "к. Геодезическая долгота центральной точки, град.мин.сек",
            wgt: null,
            data: "amidLong"
        }]
}

var anglesK = {
    label: "",
    index: "",
    parent: "",
    list: [{
        type: "degree",
        label: "а. Геодезическая широта северо-западного угла, град",
        wgt: null,
        data: "anwlat"

    },
        {
            type: "degree",
            label: "б. Геодезическая долгота северо-западного угла, град",
            wgt: null,
            data: "anwlong"
        },
        {
            type: "degree",
            label: "в. Геодезическая широта северо-восточного угла, град",
            wgt: null,
            data: "anelat"
        },
        {
            type: "degree",
            label: "г. Геодезическая долгота северо-восточного угла, град",
            wgt: null,
            data: "anelong"
        },
        {
            type: "degree",
            label: "д. Геодезическая широта юго-восточного угла, град",
            wgt: null,
            data: "aselat"
        },
        {
            type: "degree",
            label: "е. Геодезическая долгота юго-восточного угла, град",
            wgt: null,
            data: "aselong"
        },
        {
            type: "degree",
            label: "ж. Геодезическая широта юго-западного угла, град",
            wgt: null,
            data: "aswlat"
        },
        {
            type: "degree",
            label: "з. Геодезическая долгота юго-западного угла, град",
            wgt: null,
            data: "aswlong"
        }]
}
