<template>
    <div class="box">
        <div class="container">
            <v-chart :options="polar1" autoresize />
        </div>
        <div class="container">
            <v-chart :options="polar2" autoresize />
        </div>
        <div class="container">
            <v-chart :options="polar3" autoresize />
        </div>
        <div class="container">
            <v-chart :options="polar4" />
        </div>
    </div>
</template>

<script>
import 'echarts/lib/chart/line'; // 折线图
import 'echarts/lib/chart/bar'; // 柱状图
import 'echarts/lib/chart/pie'; // 饼图
import 'echarts/lib/component/tooltip'; // 提示
import 'echarts/lib/component/legend'; // 按钮
import 'echarts/lib/component/title'; // 标题
import 'echarts/lib/component/markLine'; // markLine 堆叠柱状图上方的虚线
export default {
    data() {
        return {
            // 柱状图
            polar1: {
                title: {
                    text: '柱状图',
                    left: 'left'
                },
                color: ['#3398DB'],
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                        axisTick: {
                            alignWithLabel: true
                        }
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '直接访问',
                        type: 'bar',
                        barWidth: '60%',
                        data: [10, 52, 200, 334, 390, 330, 220]
                    }
                ]
            },
            // 玫瑰图
            polar2: {
                title: {
                    text: '南丁格尔玫瑰图',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'item',
                    formatter: '{a} <br/>{b} : {c} ({d}%)'
                },
                legend: {
                    left: 'center',
                    top: 'bottom',
                    data: ['rose1', 'rose2', 'rose3', 'rose4', 'rose5', 'rose6', 'rose7', 'rose8']
                },
                toolbox: {
                    show: true,
                    feature: {
                        mark: { show: true },
                        dataView: { show: true, readOnly: false },
                        magicType: {
                            show: true,
                            type: ['pie', 'funnel']
                        },
                        restore: { show: true },
                        saveAsImage: { show: true }
                    }
                },
                series: [
                    {
                        name: '半径模式',
                        type: 'pie',
                        radius: [20, 110],
                        center: ['25%', '50%'],
                        roseType: 'radius',
                        label: {
                            show: false
                        },
                        emphasis: {
                            label: {
                                show: true
                            }
                        },
                        data: [
                            { value: 10, name: 'rose1' },
                            { value: 5, name: 'rose2' },
                            { value: 15, name: 'rose3' },
                            { value: 25, name: 'rose4' },
                            { value: 20, name: 'rose5' },
                            { value: 35, name: 'rose6' },
                            { value: 30, name: 'rose7' },
                            { value: 40, name: 'rose8' }
                        ]
                    },
                    {
                        name: '面积模式',
                        type: 'pie',
                        radius: [30, 110],
                        center: ['75%', '50%'],
                        roseType: 'area',
                        data: [
                            { value: 10, name: 'rose1' },
                            { value: 5, name: 'rose2' },
                            { value: 15, name: 'rose3' },
                            { value: 25, name: 'rose4' },
                            { value: 20, name: 'rose5' },
                            { value: 35, name: 'rose6' },
                            { value: 30, name: 'rose7' },
                            { value: 40, name: 'rose8' }
                        ]
                    }
                ]
            },
            // 折线图
            polar3: {
                title: {
                    text: '堆叠区域图',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'cross',
                        label: {
                            backgroundColor: '#6a7985'
                        }
                    }
                },
                legend: {
                    data: ['邮件营销', '联盟广告', '视频广告', '直接访问', '搜索引擎'],
                    left: 'center'
                },
                toolbox: {
                    feature: {
                        saveAsImage: {}
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        boundaryGap: false,
                        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '邮件营销',
                        type: 'line',
                        stack: '总量',
                        areaStyle: {},
                        data: [120, 132, 101, 134, 90, 230, 210]
                    },
                    {
                        name: '联盟广告',
                        type: 'line',
                        stack: '总量',
                        areaStyle: {},
                        data: [220, 182, 191, 234, 290, 330, 310]
                    },
                    {
                        name: '视频广告',
                        type: 'line',
                        stack: '总量',
                        areaStyle: {},
                        data: [150, 232, 201, 154, 190, 330, 410]
                    },
                    {
                        name: '直接访问',
                        type: 'line',
                        stack: '总量',
                        areaStyle: {},
                        data: [320, 332, 301, 334, 390, 330, 320]
                    },
                    {
                        name: '搜索引擎',
                        type: 'line',
                        stack: '总量',
                        label: {
                            normal: {
                                show: true,
                                position: 'top'
                            }
                        },
                        areaStyle: {},
                        data: [820, 932, 901, 934, 1290, 1330, 1320]
                    }
                ]
            },
            // 堆叠柱状图
            polar4: {
                title: {
                    text: '堆叠柱状图',
                    left: 'left'
                },
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        // 坐标轴指示器，坐标轴触发有效
                        type: 'shadow' // 默认为直线，可选为：'line' | 'shadow'
                    }
                },
                legend: {
                    data: ['直接访问', '邮件营销', '联盟广告', '视频广告', '搜索引擎', '百度', '谷歌', '必应', '其他']
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis: [
                    {
                        type: 'category',
                        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: '直接访问',
                        type: 'bar',
                        data: [320, 332, 301, 334, 390, 330, 320]
                    },
                    {
                        name: '邮件营销',
                        type: 'bar',
                        stack: '广告',
                        data: [120, 132, 101, 134, 90, 230, 210]
                    },
                    {
                        name: '联盟广告',
                        type: 'bar',
                        stack: '广告',
                        data: [220, 182, 191, 234, 290, 330, 310]
                    },
                    {
                        name: '视频广告',
                        type: 'bar',
                        stack: '广告',
                        data: [150, 232, 201, 154, 190, 330, 410]
                    },
                    {
                        name: '搜索引擎',
                        type: 'bar',
                        data: [862, 1018, 964, 1026, 1679, 1600, 1570],
                        markLine: {
                            lineStyle: {
                                type: 'dashed'
                            },
                            data: [[{ type: 'min' }, { type: 'max' }]]
                        }
                    },
                    {
                        name: '百度',
                        type: 'bar',
                        barWidth: 5,
                        stack: '搜索引擎',
                        data: [620, 732, 701, 734, 1090, 1130, 1120]
                    },
                    {
                        name: '谷歌',
                        type: 'bar',
                        stack: '搜索引擎',
                        data: [120, 132, 101, 134, 290, 230, 220]
                    },
                    {
                        name: '必应',
                        type: 'bar',
                        stack: '搜索引擎',
                        data: [60, 72, 71, 74, 190, 130, 110]
                    },
                    {
                        name: '其他',
                        type: 'bar',
                        stack: '搜索引擎',
                        data: [62, 82, 91, 84, 109, 110, 120]
                    }
                ]
            }
        };
    }
};
</script>

<style lang="less" scoped>
.box {
    display: flex;
    flex-flow: wrap;
}
.container {
    width: 49%;
    height: 49%;
}
/**
 * 默认尺寸为 600px×400px，如果想让图表响应尺寸变化，可以像下面这样
 * 把尺寸设为百分比值（同时请记得为容器设置尺寸）。
 */
.echarts {
    width: 100%;
    height: 100%;
}
</style>