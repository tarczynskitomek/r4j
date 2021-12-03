package it.tarczynski.r4j.domain.product

import it.tarczynski.r4j.domain.TimeMachine

class PricingPolicy(
    private val timeMachine: TimeMachine,
) {

    fun currentBestPrice(
        prices: Set<Price>,
    ): Price? {
        return prices
            .takeIf { it.isNotEmpty() }
            ?.let {
                if (seriousBusinessConditionMatched()) {
                    it.firstOrNull()
                } else {
                    it.lastOrNull()
                }
            }
    }

    private fun seriousBusinessConditionMatched(): Boolean {
        return timeMachine.currentDay().value % 2 == 0
    }
}
