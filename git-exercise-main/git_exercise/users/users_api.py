from flask import Blueprint, abort, jsonify

from git_exercise.users.users_gateway import UsersGateway


def users_api(users_gateway: UsersGateway) -> Blueprint:
    api = Blueprint("users_api", __name__)

    @api.route("/users")
    def list_all():
        return jsonify(users_gateway.list())

    @api.route("/users/<int:user_id>")
    def find(user_id: int):
        user = users_gateway.find(user_id)
        if user is None:
            abort(404)

        return jsonify(user)


    return api
