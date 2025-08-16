  // 全局变量
    const urlParams = new URLSearchParams(window.location.search);
    const stockCode = urlParams.get('code') || '';
    let stockName = urlParams.get('name') || '';
    let chart = null;
    let currentDays = 60;

    // 初始化页面
    document.addEventListener('DOMContentLoaded', function() {
        // 设置股票基本信息
        document.getElementById('stockCode').textContent = stockCode;
        document.getElementById('stockName').textContent = stockName || `${stockCode}股票`;

        // 如果没有股票名称，尝试从后端获取
        if (!stockName) {
            fetchStockBasicInfo();
        }

        // 初始化图表
        initChart();

        // 加载初始数据
        loadStockData(currentDays);

        // 绑定工具栏按钮事件
        document.querySelectorAll('.toolbar-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                // 更新按钮状态
                document.querySelectorAll('.toolbar-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');

                // 获取天数参数
                currentDays = parseInt(this.getAttribute('data-days'));

                // 加载数据
                loadStockData(currentDays);
            });
        });

        // 绑定Tab切换事件
        document.querySelectorAll('.tab').forEach(tab => {
            tab.addEventListener('click', function() {
                const tabId = this.getAttribute('data-tab');

                // 更新Tab状态
                document.querySelectorAll('.tab').forEach(t => t.classList.remove('active'));
                this.classList.add('active');

                // 更新内容显示
                document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                document.getElementById(`${tabId}-content`).classList.add('active');

                // 加载对应数据
                if (tabId === 'concept') {
                    loadConceptData();
                } else if (tabId === 'company') {
                    loadCompanyData();
                }
            });
        });

        // 窗口大小变化时重绘图表
        window.addEventListener('resize', function() {
            if (chart) {
                chart.resize();
            }
        });
    });

    // 获取股票基本信息
    function fetchStockBasicInfo() {
        fetch(`/api/stock/basic/${stockCode}`)
            .then(response => response.json())
            .then(data => {
                if (data && data.stockName) {
                    stockName = data.stockName;
                    document.getElementById('stockName').textContent = stockName;
                }
            })
            .catch(error => console.error('获取股票信息失败:', error));
    }

    // 初始化ECharts图表
    function initChart() {
        chart = echarts.init(document.getElementById('chart'));

        // 初始空配置
        chart.setOption({
            animation: false,
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                }
            },
            legend: {
                data: ['K线', '成交量', 'MA5', 'MA10', 'MA20'],
                selected: {
                    'MA5': true,
                    'MA10': true,
                    'MA20': true
                }
            },
            grid: [
                {
                    left: '10%',
                    right: '10%',
                    top: '10%',
                    height: '50%'  // K线图区域高度
                },
                {
                    left: '10%',
                    right: '10%',
                    top: '65%',
                    height: '20%'   // 成交量区域高度
                }
            ],
            xAxis: [
                {
                    type: 'category',
                    data: [],
                    scale: true,
                    boundaryGap: false,
                    axisLine: { onZero: false },
                    splitLine: { show: false },
                    splitNumber: 20,
                    min: 'dataMin',
                    max: 'dataMax',
                    gridIndex: 0
                },
                {
                    type: 'category',
                    gridIndex: 1,
                    data: [],
                    scale: true,
                    boundaryGap: false,
                    axisLine: { onZero: false },
                    axisTick: { show: false },
                    splitLine: { show: false },
                    axisLabel: { show: false },
                    splitNumber: 20,
                    min: 'dataMin',
                    max: 'dataMax'
                }
            ],
            yAxis: [
                {
                    scale: true,
                    gridIndex: 0,
                    splitArea: {
                        show: true
                    }
                },
                {
                    scale: true,
                    gridIndex: 1,
                    splitNumber: 2,
                    axisLabel: { show: false },
                    axisLine: { show: false },
                    axisTick: { show: false },
                    splitLine: { show: false }
                }
            ],
            dataZoom: [
                {
                    type: 'inside',
                    xAxisIndex: [0, 1],
                    start: 0,
                    end: 100
                },
                {
                    show: true,
                    xAxisIndex: [0, 1],
                    type: 'slider',
                    bottom: '5%',
                    start: 0,
                    end: 100
                }
            ],
            series: [
                {
                    name: 'K线',
                    type: 'candlestick',
                    data: [],
                    itemStyle: {
                        color: '#f44336',
                        color0: '#4CAF50',
                        borderColor: '#f44336',
                        borderColor0: '#4CAF50'
                    },
                    xAxisIndex: 0,
                    yAxisIndex: 0
                },
                {
                    name: '成交量',
                    type: 'bar',
                    xAxisIndex: 1,
                    yAxisIndex: 1,
                    data: [],
                    itemStyle: {
                        color: function(params) {
                            // 根据涨跌显示不同颜色
                            const data = chart.getOption().series[0].data;
                            if (data && data[params.dataIndex]) {
                                const item = data[params.dataIndex];
                                return item[1] >= item[0] ? '#f44336' : '#4CAF50';
                            }
                            return '#5470C6';
                        }
                    },
                    barWidth: '60%'
                }
            ]
        });
    }

    // 加载股票数据
    function loadStockData(days) {
        showLoading();

        // 获取K线数据
        fetch(`/api/stock/kline?code=${stockCode}&days=${days}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('网络响应异常');
                }
                return response.json();
            })
            .then(data => {
                if (data && data.length > 0) {
                    // 更新股票基本信息
                    updateStockInfo(data[data.length-1]);

                    // 渲染K线图
                    renderChart(data);
                } else {
                    console.log('暂无数据');
                }
            })
            .catch(error => {
                console.error('加载数据失败:', error);
            })
            .finally(() => {
                hideLoading();
            });
    }

      // 加载概念数据
      function loadConceptData() {
          const conceptList = document.getElementById('conceptList');
          conceptList.innerHTML = '<div class="loading-spinner"></div>';

          // 调用后端API获取概念数据
          fetch(`/api/stock/concept/${stockCode}`)
              .then(response => {
                  if (!response.ok) {
                      throw new Error('网络响应异常');
                  }
                  return response.json();
              })
              .then(concepts => {
                  if (concepts && concepts.length > 0) {
                      renderConcepts(concepts);
                  } else {
                      conceptList.innerHTML = '<div class="no-data">暂无概念数据</div>';
                  }
              })
              .catch(error => {
                  console.error('加载概念数据失败:', error);
                  conceptList.innerHTML = `<div class="error">加载概念数据失败: ${error.message}</div>`;
              });
      }

      // 渲染概念列表
      function renderConcepts(concepts) {
          const conceptList = document.getElementById('conceptList');
          conceptList.innerHTML = '';

          concepts.forEach((concept, index) => {
              const item = document.createElement('div');
              item.className = 'concept-item';
              item.textContent = concept.name;
              item.dataset.index = index;

              const detail = document.createElement('div');
              detail.className = 'concept-detail';
              detail.innerHTML = `
                  <div class="concept-detail-title">概念领涨：${concept.conceptLeadingStocks}</div>
                  <div class="concept-detail-content">${concept.description}</div>
              `;

              item.addEventListener('click', function() {
                  // 切换当前概念的active状态
                  const wasActive = this.classList.contains('active');

                  // 移除所有概念的active状态
                  document.querySelectorAll('.concept-item').forEach(i => {
                      i.classList.remove('active');
                  });
                  document.querySelectorAll('.concept-detail').forEach(d => {
                      d.classList.remove('active');
                  });

                  // 如果之前不是active状态，则设置为active
                  if (!wasActive) {
                      this.classList.add('active');
                      detail.classList.add('active');
                  }
              });

              conceptList.appendChild(item);
              conceptList.appendChild(detail);
          });
      }


    // 加载公司资料
    function loadCompanyData() {
        const companyInfo = document.getElementById('companyInfo');
        companyInfo.innerHTML = '<div class="loading-spinner"></div>';

        // 模拟API请求
        setTimeout(() => {
            // 这里应该是从API获取数据，这里用模拟数据
            const mockData = {
                companyName: "某某科技股份有限公司",
                establishDate: "2010年5月20日",
                registeredCapital: "5亿元人民币",
                legalRepresentative: "张三",
                mainBusiness: "人工智能、大数据、云计算技术的研发与应用",
                address: "北京市海淀区科技园路88号",
                introduction: "某某科技是一家专注于人工智能和大数据技术的高科技企业，致力于为企业提供智能化解决方案。公司拥有多项自主知识产权，在行业内处于领先地位。"
            };

            companyInfo.innerHTML = `
                <div class="info-item">
                    <div class="info-label">公司全称</div>
                    <div class="info-content">${mockData.companyName}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">成立日期</div>
                    <div class="info-content">${mockData.establishDate}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">注册资本</div>
                    <div class="info-content">${mockData.registeredCapital}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">法定代表人</div>
                    <div class="info-content">${mockData.legalRepresentative}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">主营业务</div>
                    <div class="info-content">${mockData.mainBusiness}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">公司地址</div>
                    <div class="info-content">${mockData.address}</div>
                </div>
                <div class="info-item">
                    <div class="info-label">公司简介</div>
                    <div class="info-content">${mockData.introduction}</div>
                </div>
            `;
        }, 500);
    }

    // 更新股票基本信息
    function updateStockInfo(latestData) {
        const currentPrice = latestData.closePrice;
        const percent = latestData.percent;

        document.getElementById('currentPrice').textContent = currentPrice.toFixed(2);
        const changeElement = document.getElementById('priceChange');
        changeElement.textContent = (percent >= 0 ? '+' : '') + percent.toFixed(2) + '%';
        changeElement.className = percent >= 0 ? 'price-change positive' : 'price-change negative';
    }

    // 渲染K线图
    function renderChart(data) {
        const dates = data.map(item => item.date);
        const values = data.map(item => [
            item.open,
            item.closePrice,
            item.low,
            item.high
        ]);
        const volumes = data.map(item => item.volume);

        // 提取MA5、MA10、MA20数据
        const ma5 = data.map(item => item.ma5 || null);
        const ma10 = data.map(item => item.ma10 || null);
        const ma20 = data.map(item => item.ma20 || null);

        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function(params) {
                    const item = data[params[0].dataIndex];
                    const last = data[data.length-1];
                    const days = data.length-params[0].dataIndex;
                    const diff = last.closePrice - item.closePrice;
                    const rate = ((diff / item.closePrice) * 100).toFixed(2);
                    const rateColor = rate > 0 ? '#f44336' : '#4CAF50';
                    const sign = rate >= 0 ? '+' : '';
                    const color = item.closePrice >= item.open ? '#f44336' : '#4CAF50';
                    return `
                        <div style="font-size:16px;font-weight:bold;margin-bottom:10px;color:#333;">
                            ${item.date} ${stockName || stockCode}
                        </div>
                        <div style="display:flex;justify-content:space-between;width:220px;">
                            <div style="text-align:left;">
                                <div>开盘: ${item.open.toFixed(2)}</div>
                                <div>收盘: ${item.closePrice.toFixed(2)}</div>
                                <div>最高: ${item.high.toFixed(2)}</div>
                                <div>最低: ${item.low.toFixed(2)}</div>
                                <div>${days}天涨幅:
                                <span style="color:${rateColor}">
                                ${sign}${rate}%</span></div>
                            </div>
                            <div style="text-align:right;">
                                <div>涨跌: <span style="color:${color}">
                                    ${(item.percent >= 0 ? '+' : '') + item.percent.toFixed(2)}%
                                </span></div>
                                <div>成交量: ${formatVolume(item.volume)}</div>
                                <div>成交额: ${formatAmount(item.amount)}</div>
                            </div>
                        </div>
                    `;
                }
            },
            xAxis: [
                {
                    data: dates,
                    axisLabel: {
                        formatter: function(value) {
                            const date = new Date(value);
                            return `${date.getMonth()+1}/${date.getDate()}`;
                        }
                    }
                },
                {
                    data: dates
                }
            ],
            series: [
                {
                    data: values
                },
                {
                    data: volumes
                },
                // MA5均线
                {
                    name: 'MA5',
                    type: 'line',
                    data: ma5,
                    smooth: true,
                    lineStyle: {
                        width: 1,
                        color: '#FF7F50'
                    },
                    symbol: 'none',
                    xAxisIndex: 0,
                    yAxisIndex: 0
                },
                // MA10均线
                {
                    name: 'MA10',
                    type: 'line',
                    data: ma10,
                    smooth: true,
                    lineStyle: {
                        width: 1,
                        color: '#6A5ACD'
                    },
                    symbol: 'none',
                    xAxisIndex: 0,
                    yAxisIndex: 0
                },
                // MA20均线
                {
                    name: 'MA20',
                    type: 'line',
                    data: ma20,
                    smooth: true,
                    lineStyle: {
                        width: 1,
                        color: '#20B2AA'
                    },
                    symbol: 'none',
                    xAxisIndex: 0,
                    yAxisIndex: 0
                }
            ]
        };

        chart.setOption(option);
    }

    // 格式化成交量
    function formatVolume(volume) {
        if (volume >= 1000000) {
            return (volume / 1000000).toFixed(2) + '万手';
        } else if (volume >= 10000) {
            return (volume / 10000).toFixed(2) + '万手';
        }
        return volume.toFixed(0) + '手';
    }

    // 格式化成交额
    function formatAmount(amount) {
        if (amount >= 100000000) {
            return (amount / 100000000).toFixed(2) + '亿';
        } else if (amount >= 10000) {
            return (amount / 10000).toFixed(2) + '万';
        }
        return amount.toFixed(2);
    }

    // 显示加载状态
    function showLoading() {
        document.getElementById('chartLoading').style.display = 'flex';
    }

    // 隐藏加载状态
    function hideLoading() {
        document.getElementById('chartLoading').style.display = 'none';
    }