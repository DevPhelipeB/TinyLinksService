// Script de inicialização do MongoDB para o TinyLinks Service
// Este script é executado automaticamente quando o container MongoDB é criado

db = db.getSiblingDB('tinylinks');

db.createUser({
  user: 'tinylinks_user',
  pwd: 'tinylinks_password',
  roles: [
    {
      role: 'readWrite',
      db: 'tinylinks'
    }
  ]
});

db.createCollection('links');

db.links.createIndex({ "code": 1 }, { unique: true });
db.links.createIndex({ "userId": 1, "deleted": 1 });
db.links.createIndex({ "userId": 1, "createdAt": 1 });
db.links.createIndex({ "deleted": 1 });
db.links.createIndex({ "createdAt": 1 });

db.links.insertOne({
  _id: ObjectId(),
  code: "example",
  userId: "tester",
  originalUrl: "https://github.com/DevPhelipeB",
  createdAt: new Date(),
  updatedAt: new Date(),
  deleted: false,
  visits: 0
});

print('MongoDB inicializado com sucesso para o TinyLinks Service!');
print('Usuário criado: tinylinks_user');
print('Coleção criada: links');
print('Índices criados para otimização de consultas');
