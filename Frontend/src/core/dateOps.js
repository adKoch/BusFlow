

function getEpochFromChosenTime() {
    return new Date(this.aggregationDate).getTime() / 1000 + this.getSecondsFromChosenTime()
}

export function dateAfterSecondsPassed(date, secondsPassed) {

}