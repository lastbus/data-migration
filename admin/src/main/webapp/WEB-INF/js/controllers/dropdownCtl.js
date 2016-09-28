'use strict';
/**
 * Created by MK33 on 2016/9/19.
 */

app.controller('dropdownCtl', function($scope, $http, $modal, $log, $filter) {
    $scope.envs = ["pre", "test"];
    $scope.success = true;
    // for test, this should be from server
    $scope.partitions = [
        'dt=20160912', 'dt=20160911', 'dt=20160910', 'dt=20160909','dt=20160908', 'dt=20160907'
    ];
    $scope.count = 0;
    $scope.ac = "all";
    $scope.req = '';

    $scope.open = function(index) {  //打开模态
        var table = $scope.tables[index];
        console.log(table);
        $scope.selectedTable = table;
        var html = table.partition.hasPartition ? "/templates/select.html" : "/templates/input.html";
        console.log(html);
        var modalInstance = $modal.open({
            templateUrl : html,  //指向上面创建的视图
            controller : 'partitionSelectCtl',// 初始化模态范围
            resolve : {
                selectedTable : function(){
                    return $scope.selectedTable;
                }
            }
        });
        modalInstance.result.then(function(selectedItem){
            var tmp = document.getElementById("resultTable").getElementsByTagName("tr").item(index + 1).getElementsByTagName("td");
            // console.log(tmp);
            tmp[5].textContent = selectedItem;
        }, function(){
            $log.info('Modal dismissed at: ' + new Date())
        });
    };

    $scope.refreshDataSource = function () {
        var refresh = document.getElementById("btnRefresh");
        refresh.disabled = true;
        console.log("refresh data source");
        $http.get("http://localhost:8080/refresh").then(function successCallback(response) {
            console.log(response.data);
            refresh.disabled = false;
        }, function errorCallback(response) {
            console.log(response.data);
            refresh.disabled = true
        });
    };

    $scope.choose = function(index) {
        console.log(index);
        console.log(document.getElementById(index));
        var checked = document.getElementById(index).checked;
        document.getElementById('btn' + index).disabled = !document.getElementById('btn' + index).disabled;

        if ( checked && $scope.count == 0) {
            $scope.count += 1;
            document.getElementById("importBtn").disabled = false;
        } else if (checked) {
            document.getElementById('btn' + index).disabled = false;
            $scope.count += 1
        } else if ($scope.count > 1) {
            $scope.count -= 1
        } else {
            $scope.count -= 1;
            document.getElementById("importBtn").disabled = true
        }
        console.log($scope.count)
    };

    /** 后台返回数据格式：
     *  {
     *      status："OK" / "ERROR",
     *      total: 100,
     *      count: 10,
     *      tables: [{}, {}, {}, ...]
     *  }
     *
     *  tables: [
     *      {
     *        "id": 0
     *       "env": "pre",
     *       "database": "default",
      *       "table": "lianhua_sales_details",
      *       "partition": {
      *             hasPartition: true/false,
      *             partition: ["dt=20160920", "dt=20160921", "dt=20160922"]
      *         }
      *      },
     *      ...
     *  ]
     */
    $scope.search = function() {
        var environment = $scope.environment;
        var tableName = $scope.tableName;
        if (tableName == undefined || tableName.length == 0) {
            alert("Please input search table name");
            return;
        }
        var url = "http://localhost:8080/search";
        if (environment != undefined) url += "/database/" + environment;
        url += "/table/" + tableName;
        $scope.req = url;
        url +=  "/pageSize/" + $scope.paginationConf.itemsPerPage;
        // for debug, print every request
        console.log('请求:   ' + url);
        $http.get(url).
        then(function successCallback(response) {
            $scope.success = true;
            console.log("响应: ");
            console.log(response);
            if (response.data.status = "OK") {
                console.log("OK");
                if (response.data.total == 0)
                    alert("no result");
                else {
                    $scope.paginationConf.totalItems = response.data.total;
                    $scope.tables = response.data.tables;
                }
            } else {
                console.log("ERROR");
                // $scope.tables = {};
            }
        }, function errorCallback(response) {
            $scope.success = false;
            console.log("error:");
            console.log(response);
            $scope.tables = [
                {name: "error0", partition: true},
                {name: "error1", partition: false}
            ]
        })
    };

    // pagination
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            if ($scope.tables == undefined ||$scope.tables.length == 0) return;
            console.log("currentPage: " + $scope.paginationConf.currentPage);
            console.log("totalItems: " + $scope.paginationConf.totalItems);
            console.log("itemsPerPage: " + $scope.paginationConf.itemsPerPage);
            console.log("pagesLength: " + $scope.paginationConf.pagesLength);
            // 因为要翻页，所以搜索的头部不能每次都拼接吧，做一个公共的头部，每次翻页用。

            // 构造后台请求
            var paginationReq = $scope.req +
                "/" + $scope.paginationConf.currentPage +
                "/" + $scope.paginationConf.itemsPerPage;

            console.log("pagination request: " + paginationReq);
            $http.get(paginationReq).then(function successCallback(response) {
                $scope.tables = response.data.tables;
            }, function errorCallback(response) {
                console.log("pagination request failed");
            })
        }
    };
    
    $scope.import = function(index) {
        var selectedTable = $scope.tables[index];
        console.log(selectedTable);
        var env = selectedTable.env;
        var database = selectedTable.database;
        var table = selectedTable.table;
        if (selectedTable.partition.hasPartition) {
            var t = document.getElementById("resultTable").getElementsByTagName("tr")[index + 1];
            var partition = t.getElementsByTagName("td")[5].innerHTML;
            var url = "http://localhost:8080/move/env/" + env + "/database/" + database + "/table/" + table + "/partition/" + partition;
            console.log(url);
            $http.get(url).then(function successCallback(response) {

            }, function errorCallback(response) {

            });
        } else {
            var url = "http://localhost:8080/move/env/" + env + "/database/" + database + "/table/" + table;
            console.log(url);
            $http.get(url).then(function successCallback(response) {

            }, function errorCallback(response) {

            });

        }
    };
});

