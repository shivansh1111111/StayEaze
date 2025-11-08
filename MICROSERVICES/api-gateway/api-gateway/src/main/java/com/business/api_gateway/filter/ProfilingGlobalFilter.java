package com.business.api_gateway.filter;

import com.business.api_gateway.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
public class ProfilingGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public int getOrder() {
        // -1 is response write filter, must be called before that
        return -3;
    }
	@Autowired
	private Environment env;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		log.debug("Inside ProfilingGlobalFilter");
		final long inTime=System.currentTimeMillis();

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			ServerHttpResponse originalResponse = exchange.getResponse();
			final long outTime=System.currentTimeMillis();
			String authenticationCode = exchange.getRequest().getHeaders().getFirst(Constants.AUTHENTICATION_CODE);
			if(authenticationCode==null){
				authenticationCode = exchange.getResponse().getHeaders().getFirst(Constants.AUTHENTICATION_CODE);
			}
			log.debug("API statistics1: |{}|{}|{}|{}|{}|{}",authenticationCode,
					exchange.getRequest().getURI().getPath(),
					originalResponse.getStatusCode().value(),inTime,outTime,
					(outTime-inTime)
					);

			log.debug("API statistics2: |{}|{}|{}|{}|{}|{}|{}|{}|{}|{}",exchange.getResponse().getHeaders().getFirst(Constants.WLP_NAME),
					exchange.getResponse().getHeaders().getFirst(Constants.GROUP_NAME),
					exchange.getRequest().getURI().getPath(),exchange.getResponse().getHeaders().getFirst(Constants.RESPONSE_CODE),
					inTime,outTime, (outTime-inTime), exchange.getResponse().getHeaders().getFirst(Constants.TX_NUMBER),
					exchange.getResponse().getHeaders().getFirst(Constants.USER_ID),exchange.getResponse().getHeaders().getFirst(Constants.TRACE_ID)
			);
		}));

    }

}