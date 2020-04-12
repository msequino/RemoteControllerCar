package com.example.remotecontrollercar

import java.io.Serializable

interface Action
class Throttle(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "Throttle;$x;$y"
    }
}
class Reverse(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "Reverse;$x;$y"
    }
}
class SlowDown() : Action {
    override fun toString(): String {
        return "SlowDown"
    }
}
class Steer(val x: Float, val y: Float) : Action {
    override fun toString(): String {
        return "Steer;$x;$y"
    }
}

class MessagePayload(val action: Action, val ts: String) : Serializable {

    override fun toString(): String {
        return "${action};$ts"
    }
}