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

    // pagination
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 0,
        itemsPerPage: 10,
        pagesLength: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            console.log("currentPage: " + $scope.paginationConf.currentPage);
            console.log("totalItems: " + $scope.paginationConf.totalItems);
            console.log("itemsPerPage: " + $scope.paginationConf.itemsPerPage);
            console.log("pagesLength: " + $scope.paginationConf.pagesLength);
            // 因为要翻页，所以搜索的头部不能每次都拼接吧，做一个公共的头部，每次翻页用。

            // 构造请求后台 solr：
            var paginationReq = $scope.req +
                                 "/" + $scope.paginationConf.currentPage +
                                 "/" + $scope.paginationConf.itemsPerPage;
            
            console.log("pagination request: " + paginationReq);
            $http.get(paginationReq).then(function successCallback(response) {
                
            }, function errorCallback(response) {
                
            })
            
        }
    };

    $scope.open = function(index) {  //打开模态
        var rows = document.getElementById("resultTable").getElementsByTagName("tr").item(index + 1);
        var ii = rows.getElementsByTagName('td');
        var table = ii[2].textContent;
        console.log(ii[2].textContent.trim());
        $scope.selectedTable = {name: table, partitions: $scope.partitions};
        var hasPartition = ii[3].textContent.trim();
        var html = (hasPartition.search("partition") != -1) ? "/templates/select.html" : "/templates/input.html";
        console.log(html);
        var modalInstance = $modal.open({
            templateUrl : html,  //指向上面创建的视图
            controller : 'partitionSelectCtl',// 初始化模态范围
            size : table,
            resolve : {
                selectedTable : function(){
                    return $scope.selectedTable;
                }
            }
        });
        modalInstance.size = table;
        modalInstance.result.then(function(selectedItem){
            ii[2].textContent = selectedItem;
        },function(){
            $log.info('Modal dismissed at: ' + new Date())
        });
    };

    $scope.refreshDataSource = function () {
        var refresh = document.getElementById("btnRefresh")
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
        // for debug, print every request
        console.log('请求:   ' + url);
        $http.get(url).
        then(function successCallback(response) {
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
            console.log('error: ' + response);
            $scope.tables = [
                {name: "error0", partition: true},
                {name: "error1", partition: false}
            ]
        })
    };

    $scope.all = function() {
        var u = document.getElementById("partition-select").getElementsByTagName("input")
        if ($scope.ac == "all") {
            for (p in u) {
                u[p].checked = true
            }
            $scope.ac = "clear"
        } else {
            for (p in u) {
                u[p].checked = false
            }
            $scope.ac = "all"
        }
        alert("success!")
    }
});

