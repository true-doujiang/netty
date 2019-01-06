#SimpleNioServer

事件驱动，restful

##后续计划

- 添加mvc --end,往后可能会考虑类似jfinal或者play形式的controller-action模式
- 添加orm  --考虑使用activerecord模式
- 添加aop	--借鉴jfinal2.0的aop实现
- ......

总而言之，就是造轮子-----

##运行测试

1. 运行test包下HttpServerTest主类，此类测试的基于handle的开发模式
访问localhost:20000/test即可

2. 运行mvctest包下的MvcTest主类,此类测试基于组件widget的开发模式，此模式的案例可见于(typecho4j)[http://git.oschina.net/guxingke/typecho4j]
访问localhost:20000/test即可


###NOTE
参照了WebJava的部分实现