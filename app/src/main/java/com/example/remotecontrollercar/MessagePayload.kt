package com.example.remotecontrollercar

import java.io.Serializable

interface Action
class Throttle(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "type: 'Throttle', x: $x, y: $y"
    }
}
class Reverse(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "type: 'Reverse', x: $x, y: $y"
    }
}
class SlowDown() : Action {
    override fun toString(): String {
        return "type: 'SlowDown'"
    }
}
class Steer(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "type: 'Steer', x: $x, y: $y"
    }
}

class MessagePayload(val action: Action, val ts: String) : Serializable {

    override fun toString(): String {
        return "{${action}, timestamp: $ts}"
    }
}