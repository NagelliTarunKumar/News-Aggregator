from git_exercise.users.user import User


class UsersGateway:
    users: list[User]
    
    def __init__(self):
        self.users = [
            User(id=1, name="Fred Derf"),
            User(id=2, name="Mary Yram"),
            User(id=3, name="Jane Enaj"),
            User(id=4, name="John Nhoj"),
        ]
    
    def find(self, user_id: int) -> User | None:
        for user in self.users:
            if user.id == user_id:
                return user
        
        return None
    
    def list(self):
        return self.users
