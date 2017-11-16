import random
from datetime import datetime


# Profile
class Profile:
    def __init__(self, index, last, first, middle=None):
        if middle:
            self.name = "{} {} {}".format(first, middle, last)
            self.userID = "{}{}{}{}".format(first[0].lower(), middle[0].lower(), last[0].lower(), index)
        else:
            self.name = "{} {}".format(first, last)
            self.userID = "{}{}{}".format(first[0].lower(), last[0].lower(), index)

        if (random.randint(0, 1) == 0):
            self.DOB = datetime(
                year=random.randint(1944, 2004), month=random.randint(1, 12), day=random.randint(1, 28)
            ).strftime("%Y-%m-%d")
        else:
            self.DOB = "NULL"

    def __str__(self):
        return "{},{},{}@pitt.edu,password,{},NULL\n".format(
            self.userID, self.name, self.userID, self.DOB
        )


with open('text/first.txt', 'r') as f: first = f.read().split()
with open('text/last.txt', 'r') as g: last = g.read().split()

random.shuffle(first)
random.shuffle(last)

Profiles = []
for index in range(0, 100):
    if (random.randint(0, 1) == 1):
        Profiles.append(Profile(index, last[index], first[index], first[index]))
    else:
        Profiles.append(Profile(index, last[index], first[index]))

with open('../Data/PROFILE.csv', 'w') as f:
    f.write('userID,name,email,password,date_of_birth\n')
    for profile in Profiles: f.write(str(profile))
    f.close()


# Friends
class Friend:
    def __init__(self, friend1, friend2, message):
        self.userID1 = friend1.userID
        self.userID2 = friend2.userID
        self.JDate = self.DOB = datetime(
            year=2017,
            month=random.randint(1, datetime.now().month),
            day=random.randint(1, datetime.now().day)
        ).strftime("%Y-%m-%d")
        self.message = message

    def __str__(self):
        return str("{},{},{},{}\n".format(
            self.userID1, self.userID2, self.JDate, self.message
        ))


requests = []
with open('text/requests.txt') as f: requests = f.read().split('\n')

Friends = []
Combinations = []
for friend in Profiles + Profiles + Profiles:
    if (friend, random.choice(Profiles)) not in Combinations:
        Combinations.append((friend, random.choice(Profiles)))
        Friends.append(Friend(friend, random.choice(Profiles), random.choice(requests)))

    if len(Friends) >= 200: break

with open('../Data/FRIENDS.csv', 'w') as f:
    f.write('userID1,userID2,JDate,message\n');
    for friend in Friends: f.write(str(friend))
    f.close()


# Groups
class Group:
    def __init__(self, index, name, description, members):
        self.gID = index
        self.name = name
        self.description = description
        self.members = members

    def __str__(self):
        return str('{},{},{}\n').format(self.gID, self.name, self.description)

    def memberships(self):
        ret = ""
        admin = 'Admin'
        for profile in self.members:
            ret += str('{},{},{}\n').format(self.gID, profile.userID, admin)
            admin = 'Member'
        return ret


descriptions = []
with open('text/groups.txt', 'r') as f:
    desc = f.read().split('\n')
    random.shuffle(desc)
    for line in desc:
        descriptions.append(line.split(','))

        if len(descriptions) >= 10: break

Groups = []
for idx, tuple in enumerate(descriptions):
    random.shuffle(Profiles)
    members = Profiles[0:random.randint(3, 15)]
    Groups.append(Group(idx + 1, tuple[0], tuple[1], members))

with open('../Data/GROUPS.csv', 'w') as f:
    f.write('gID,name,description\n')
    for group in Groups: f.write(str(group))
    f.close()

with open('../Data/GROUP_MEMBERSHIP.csv', 'w') as f:
    f.write('gID,userID,role\n')
    for group in Groups: f.write(group.memberships())
    f.close()


# Messages
class Message:
    def __init__(self, index, fromProfile, message, toProfile=None, toGroup=None):
        self.msgID = index
        self.fromID = fromProfile.userID
        self.message = message

        if toProfile:
            self.recipients = [toProfile]
            self.toUserID = toProfile.userID
            self.toGroupID = "NULL"
        elif toGroup:
            self.recipients = toGroup.members
            self.toGroupID = toGroup.gID
            self.toUserID = "NULL"

        self.dateSent = self.DOB = datetime(
            year=2017,
            month=random.randint(1, datetime.now().month),
            day=random.randint(1, datetime.now().day)
        ).strftime("%Y-%m-%d")

    def __str__(self):
        return "{},{},{},{},{}\n".format(self.msgID, self.fromID, self.message, self.toUserID, self.toGroupID,
                                         self.dateSent)

    def getRecipients(self):
        ret = ""
        for recipient in self.recipients:
            ret += "{},{}\n".format(self.msgID, recipient.userID)
        return ret


with open('text/messages.txt') as f: messages = f.read().split('\n')

Messages = []
for idx, friend in enumerate(Profiles + Profiles + Profiles):
    if (random.randint(0, 3) == 0):
        Messages.append(Message(idx + 1, friend, random.choice(messages), toGroup=random.choice(Groups)))
    else:
        Messages.append(Message(idx + 1, friend, random.choice(messages), toProfile=random.choice(Profiles)))

    if len(Combinations) >= 300: break

with open('../Data/MESSAGES.csv', 'w') as f:
    f.write('msgID,fromID,message,toUserID,toGroupID,dateSent\n')
    for message in Messages: f.write(str(message))
    f.close()

with open('../Data/MESSAGE_RECIPIENT.csv', 'w') as f:
    f.write('msgID,userID\n')
    for message in Messages: f.write(str(message.getRecipients()))
    f.close()
