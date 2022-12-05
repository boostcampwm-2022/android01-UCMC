package com.gta.domain.model

class CoolDownException(val cooldown: Long) : Exception()
class FirestoreException : Exception()
class DuplicatedItemException : Exception()
class DeleteFailException : Exception()
class UserNotFoundException : Exception()
