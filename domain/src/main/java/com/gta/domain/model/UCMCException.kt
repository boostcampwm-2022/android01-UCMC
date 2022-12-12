package com.gta.domain.model

class CoolDownException(val cooldown: Long) : Exception()
class FirestoreException : Exception()
class DuplicatedItemException : Exception()
class DeleteFailException : Exception()
class UserNotFoundException : Exception()
class ExpiredItemException : Exception()
class UpdateFailException : Exception()
class NotFoundDataException : Exception()