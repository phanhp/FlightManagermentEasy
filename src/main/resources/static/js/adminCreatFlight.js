$(document).ready(function () {
//BASE FUNCTION
    function updateChild(dataList, idChild, baseString) {
        idChild.empty();
        let defaultOption = '<option class="auto-option" value="0" selected>' + baseString + '</option>';
        idChild.append(defaultOption);
        for (let i = 0; i < dataList.length; i++) {
            let data = dataList[i];
            let newOption = '<option class="select-option" value="' + data.id + '">' + data.name + '</option>';
            idChild.append(newOption);
        }
    }

    function updateParent(data, idParent) {
        if (data != null) {
            idParent.val(data.id);
        } else {
            idParent.val(0);
        }
    }

    function resolveChildOnEvent(idChangeVal, idChildOption, idTimeVal, urlChild, childBaseString) {
        $.ajax({
            type: 'GET',
            url: urlChild,
            data: {
                idChangeVal: idChangeVal,
                idTimeVal: idTimeVal
            },
            success: function (dataList) {
                updateChild(dataList, idChildOption, childBaseString);
            },
            error: function () {
                alert('error');
            }
        });
    }

    function resolveParentOnEvent(idChangeVal, idParentOption, idTimeVal, urlParent) {
        $.ajax({
            type: 'GET',
            url: urlParent,
            data: {
                idChangeVal: idChangeVal,
                idTimeVal: idTimeVal
            },
            success: function (data) {
                updateParent(data, idParentOption);
            },
            error: function () {
                alert('error');
            }
        });
    }

    function getRouteListByTime(idCountryVal, idCityVal, idAirportVal, idRouteOption, idTimeVal, baseString) {
        $.ajax({
            type: 'GET',
            url: '/ajax/getRouteListByTime',
            data: {
                idCountryVal: idCountryVal,
                idCityVal: idCityVal,
                idAirportVal: idAirportVal,
                idTimeVal: idTimeVal
            },
            success: function (routeList) {
                updateChild(routeList, idRouteOption, baseString);
            },
            error: function () {
                alert('error');
            }
        })
    }

    function getAppropriateAircraft(departureTime, arrivalTime, idAircraftOption) {
        $.ajax({
            type: 'GET',
            url: '/ajax/getAircraftListByTime',
            data: {
                departureTime: departureTime,
                arrivalTime: arrivalTime,
            },
            success: function (aircraftList) {
                updateChild(aircraftList, idAircraftOption, 'Choose Aircraft');
            },
            error: function () {
                alert('error');
            }
        })
    }

//USE
    //Change Time Event
    $('#departureTime').on("input", function () {
        let timeVal = $('#departureTime').val();
        let countryVal = $('#departureCountry').val();
        let cityVal = $('#departureCity').val();
        let airportVal = $('#departureAirport').val();
        let routeOption = $('#departureRoute');

        getRouteListByTime(countryVal, cityVal, airportVal, routeOption, timeVal, 'Choose Departure Route');

        let timeVal2 = $('#arrivalTime').val();
        let aircraftOption = $('#aircraft');
        getAppropriateAircraft(timeVal, timeVal2, aircraftOption);

    });

    $('#arrivalTime').on("input", function () {
        let timeVal = $('#arrivalTime').val();
        let countryVal = $('#arrivalCountry').val();
        let cityVal = $('#arrivalCity').val();
        let airportVal = $('#arrivalAirport').val();
        let routeOption = $('#arrivalRoute');

        getRouteListByTime(countryVal, cityVal, airportVal, routeOption, timeVal, 'Choose Arrival Route');

        let timeVal2 = $('#departureTime').val();
        let aircraftOption = $('#aircraft');
        getAppropriateAircraft(timeVal2, timeVal, aircraftOption);
    });

    //Change Country Event
    $('#departureCountry').change(function () {
        let timeVal = $('#departureTime').val();
        let countryVal = $('#departureCountry').val();
        let cityOption = $('#departureCity');
        let airportOption = $('#departureAirport');
        let routeOption = $('#departureRoute');

        resolveChildOnEvent(countryVal, cityOption, timeVal,
            '/ajax/getCityListByCountryId', 'Choose Departure City');

        resolveChildOnEvent(countryVal, airportOption, timeVal,
            '/ajax/getAirportListByCountryId', 'Choose Departure Airport');

        resolveChildOnEvent(countryVal, routeOption, timeVal,
            '/ajax/getRouteListByCountryId', 'Choose Departure Route');
    });

    $('#arrivalCountry').change(function () {
        let timeVal = $('#arrivalTime').val();
        let countryVal = $('#arrivalCountry').val();
        let cityOption = $('#arrivalCity');
        let airportOption = $('#arrivalAirport');
        let routeOption = $('#arrivalRoute');

        resolveChildOnEvent(countryVal, cityOption, timeVal,
            '/ajax/getCityListByCountryId', 'Choose Arrival City');

        resolveChildOnEvent(countryVal, airportOption, timeVal,
            '/ajax/getAirportListByCountryId', 'Choose Arrival Airport');

        resolveChildOnEvent(countryVal, routeOption, timeVal,
            '/ajax/getRouteListByCountryId', 'Choose Arrival Route');
    });

    //Change City Event
    $('#departureCity').change(function () {
        let timeVal = $('#departureTime').val();
        let countryOption = $('#departureCountry');
        let cityVal = $('#departureCity').val();
        let airportOption = $('#departureAirport');
        let routeOption = $('#departureRoute');

        resolveParentOnEvent(cityVal, countryOption, timeVal,
            '/ajax/getCountryByCityId');

        resolveChildOnEvent(cityVal, airportOption, timeVal,
            '/ajax/getAirportListByCityId', 'Choose Departure Airport');

        resolveChildOnEvent(cityVal, routeOption, timeVal,
            '/ajax/getRouteListByCityId', 'Choose Departure Route');
    });

    $('#arrivalCity').change(function () {
        let timeVal = $('#arrivalTime').val();
        let countryOption = $('#arrivalCountry');
        let cityVal = $('#arrivalCity').val();
        let airportOption = $('#arrivalAirport');
        let routeOption = $('#arrivalRoute');

        resolveParentOnEvent(cityVal, countryOption, timeVal,
            '/ajax/getCountryByCityId');

        resolveChildOnEvent(cityVal, airportOption, timeVal,
            '/ajax/getAirportListByCityId', 'Choose Arrival Airport');

        resolveChildOnEvent(cityVal, routeOption, timeVal,
            '/ajax/getRouteListByCityId', 'Choose Arrival Route');
    });

    //Change Airport Event
    $('#departureAirport').change(function () {
        let timeVal = $('#departureTime').val();
        let countryOption = $('#departureCountry');
        let cityOption = $('#departureCity');
        let airportVal = $('#departureAirport').val();
        let routeOption = $('#departureRoute');

        resolveParentOnEvent(airportVal, countryOption, timeVal,
            '/ajax/getCountryByAirportId');

        resolveParentOnEvent(airportVal, cityOption, timeVal,
            '/ajax/getCityByAirportId');

        resolveChildOnEvent(airportVal, routeOption, timeVal,
            '/ajax/getRouteListByAirportId', 'Choose Departure Route');
    });

    $('#arrivalAirport').change(function () {
        let timeVal = $('#arrivalTime').val();
        let countryOption = $('#arrivalCountry');
        let cityOption = $('#arrivalCity');
        let airportVal = $('#arrivalAirport').val();
        let routeOption = $('#arrivalRoute');

        resolveParentOnEvent(airportVal, countryOption, timeVal,
            '/ajax/getCountryByAirportId');

        resolveParentOnEvent(airportVal, cityOption, timeVal,
            '/ajax/getCityByAirportId');

        resolveChildOnEvent(airportVal, routeOption, timeVal,
            '/ajax/getRouteListByAirportId', 'Choose Arrival Route');
    });


    //Change Route Event
    $('#departureRoute').change(function () {
        let timeVal = $('#departureTime').val();
        let countryOption = $('#departureCountry');
        let cityOption = $('#departureCity');
        let airportOption = $('#departureAirport');
        let routeVal = $('#departureRoute').val();

        resolveParentOnEvent(routeVal, countryOption, timeVal,
            '/ajax/getCountryByRouteId');

        resolveParentOnEvent(routeVal, cityOption, timeVal,
            '/ajax/getCityByRouteId');

        resolveParentOnEvent(routeVal, airportOption, timeVal,
            '/ajax/getAirportByRouteId');
    });

    $('#arrivalRoute').change(function () {
        let timeVal = $('#arrivalTime').val();
        let countryOption = $('#arrivalCountry');
        let cityOption = $('#arrivalCity');
        let airportOption = $('#arrivalAirport');
        let routeVal = $('#arrivalRoute').val();

        resolveParentOnEvent(routeVal, countryOption, timeVal,
            '/ajax/getCountryByRouteId');

        resolveParentOnEvent(routeVal, cityOption, timeVal,
            '/ajax/getCityByRouteId');

        resolveParentOnEvent(routeVal, airportOption, timeVal,
            '/ajax/getAirportByRouteId');
    });

    document.getElementById('flightCRUDForm').addEventListener('submit', function (event) {
        event.preventDefault();
        const aircraftId = document.getElementById('aircraft').value;
        const departureRouteId = document.getElementById('departureRoute').value;
        const arrivalRouteId = document.getElementById('arrivalRoute').value;
        if (aircraftId !== '0' && departureRouteId !== '0' && arrivalRouteId !== '0') {
            document.getElementById('flightCRUDForm').submit();
        }
    });
})