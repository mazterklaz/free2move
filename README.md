# Run the program
### With IntelliJ
Pass VM arguments for start and end date:
`-DSTART_DATE=1970-01-01 -DEND_DATE=2078-05-10`
Run the class `main.Free2MoveApp`

### With sbt
Go to sbt terminal:
```bash
sbt
```
Then run the main app
```bash
runMain main.Free2MoveApp
```

### With EMR
Create a fat jar with
``` 
sbt assembly
```
Then you can copy the jar to S3 and run your emr cluster.

Example with command line:
```
aws emr create-cluster \
	 --enable-debugging \
	 --log-uri s3://path-to-logs \
     --name "Free2MoveApp" \
     --release-label emr-6.4.0 \
     --instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=c1.xlarge InstanceGroupType=CORE,InstanceCount=2,InstanceType=c1.medium \
     --applications Name=Spark \
     --use-default-roles \
     --steps Type=Spark,Name="Free2MoveApp",ActionOnFailure=CONTINUE,Args=[--class,main.Free2MoveApp,s3://path-to-jar/free2move.jar] \
     --ec2-attributes SubnetId=subnet-xxx,EmrManagedMasterSecurityGroup=sg-xxx,EmrManagedSlaveSecurityGroup=sg-xxx,ServiceAccessSecurityGroup=sg-xxx \
     --auto-terminate
```


# TECHNICAL TEST : ANALYZE E-COMMERCE DATA

You were recently hired by an E-commerce company. Your mission is to provide insights on sales.

There are four datasets :
* *Products*: a list of available products.
* *Items*: a list of items.
* *Orders*: a list of customer orders on the website.
* *Customers*: a list of customers.

Centralize your projet and code in a Github repository and provide the url once the test is completed.

**To Dos**
1. Get the four datasets into Spark
2. Each day we want to compute summary statistics by customers every day (number of spendings, number of orders, ...)
Create a Spark script to compute for a given day these summary statistics.
3. Run that script over a defined time range to identify the top customers.
4. How many customers are repeaters (order during 2 or more different days) ?
5. Optionnal : If you want to show more skills you have, add anything you find usefull :
	- To automate this and make it run every day
	- To bring it in a "Infra-as-Code" way
	- To add real-time on anything you want
	- Anything you want to show
