package com.exstas.kalaha.factory;

import com.exstas.kalaha.*;
import com.exstas.kalaha.kalaha_move_chain.ProcessOneStoneHandler;
import com.exstas.kalaha.kalaha_move_chain.SowPitsHandler;
import com.exstas.kalaha.kalaha_move_chain.TryToResolveWinnerHandler;
import com.exstas.kalaha.kalaha_move_chain.ValidateKalahaMoveHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FactoryConfiguration {

    @Bean
    public KalahaFactory getKalahaFactory() {
        return new KalahaFactory();
    }

    @Bean
    public PitsFactory getPitsFactory() {
        return new PitsFactory();
    }

    @Bean
    public KalahaBuilder getKalahaBuilder() {
        return new KalahaBuilder();
    }

    @Bean
    public PitService getPitService() {
        return new PitService();
    }

    @Bean
    public ValidateKalahaMoveHandler getValidateKalahaMoveHandler() {
        return new ValidateKalahaMoveHandler();
    }

    @Bean
    public SowPitsHandler getSowPitsHandler() {
        return new SowPitsHandler();
    }

    @Bean
    public ProcessOneStoneHandler getProcessOneStoneHandler() {
        return new ProcessOneStoneHandler();
    }

    @Bean
    public TryToResolveWinnerHandler getTryToResolveWinnerHandler() {
        return new TryToResolveWinnerHandler();
    }
}
