# Tests avec Kafka

## Exemple Avro

Lecture d'un document avro avec un record générique ou un schéma ayant un champ commun.

Classes du package: `io.onurb.examples.avro`


## Exemples avec transform et state stores

Contexte:
* un topic alimenté avec des données de référence : un user (clé) et sa région (valeur)
* un topic alimenté avec des données temps réel de clics : nb clics (valeur) par user (valeur)

But: alimenter un topic avec des données enrichies : clé=user, valeur=(user, region, clics)


* Exemple 1: utilisation simple d'un `transform` (pour aller chercher des données dans le state store et enrichir les données):
  * Classes dans le packages: `io.onurb.examples.kafka.transform`
  * topic de référence matérialisé sous la forme d'un state store
  * méthode `transform` utilisé pour enrichir les données du topic des données temps réel à partir des données stockées dans le state store


* Exemple 2: création de 2 stores
  * 1 avec les données enrichie mais incomplète (on considère que c'est complet qu'on a reçu au moins 3 messages pour un même user)
  * 1 store avec les regions (idem 1er exemples)
  * quand les données sont complètes, envoie vers un topic final


```bash
# Création des topics

kafka-topics --zookeeper localhost:2181 --create --topic user-region-topic --replication-factor 1 --partitions 1 \
   --config min.insync.replicas=1 --config cleanup.policy=compact

kafka-topics --zookeeper localhost:2181 --create --topic region-clicks-store --replication-factor 1 --partitions 1 \
   --config min.insync.replicas=1 --config cleanup.policy=compact


# Pour tester une modif du référentiel user/region (pour voir que c'est bien pris en compte dans le state store de données de référence)
kafka-console-producer --broker-list localhost:9092 --topic user-region-topic --property "parse.key=true" --property "key.separator=:"
> ed:CA

```


## Commandes utiles

```bash
kafka-topics --zookeeper localhost:2181 --list
```