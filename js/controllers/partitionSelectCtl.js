

app.controller('partitionSelectCtl', function($scope, $modalInstance, selectedTable) {
    $scope.table = selectedTable.name;
    $scope.ac = 'all';
    $scope.partitions = selectedTable.partitions;
    $scope.selected = {
        partition : $scope.partitions[0]
    };
    $scope.ok = function(){
        var p = document.getElementById('partition-select0').getElementsByTagName('input');
        var ps = [];
        for (i = 0; i < p.length;  i++) {
            ;if (p[i].checked) {
                ps.push(p[i].value)
            }
        }
        console.log(ps);
        $modalInstance.close(ps); //关闭并返回当前选项
    };
    
    $scope.OK = function () {
        console.log($scope.recordNumber);
        $modalInstance.close($scope.recordNumber);
    };
    $scope.cancel = function(){
        $modalInstance.dismiss('cancel'); // 退出
    };

    $scope.all = function () {
        var input = document.getElementById("partition-select0").getElementsByTagName("input")
        if ($scope.ac == "all") {
            for (i in input) {
                input[i].checked = true;
            }
            $scope.ac = "clear";
        } else {
            for (i in input) {
                input[i].checked =false;
            }
            $scope.ac = "all";
        }
    }
});