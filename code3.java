import java.util.*;
import java.io.*;
import java.math.*;


/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {
    public static int distancies[][];
    public static int factoryCount;

    //Struct
    public static class BasicInfo {
        public int propietari, tropes, produccio;

        public BasicInfo(int propietari, int tropes, int produccio){
            this.propietari = propietari;
            this.tropes = tropes;
            this.produccio = produccio;
        }

        public BasicInfo(final BasicInfo bi){
            this.propietari = bi.propietari;
            this.tropes = bi.tropes;
            this.produccio = bi.produccio;
        }

        public int getPropietari(){
            return this.propietari;
        }
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        factoryCount = in.nextInt(); // the number of factories
        int linkCount = in.nextInt(); // the number of links between factories
        int torn = 0;
        int num_bombes = 2;

        distancies = new int[factoryCount][factoryCount];

        // Initialize the distances from factories
        for(int i=0;i<factoryCount; i++){
            for(int j=0;j<factoryCount; j++){
                distancies[i][j]=1000;
            }           
        }

        for (int i = 0; i < linkCount; i++) {
            int factory1 = in.nextInt();
            int factory2 = in.nextInt();
            int distance = in.nextInt();

            // Communications are bidirectionals
            distancies[factory1][factory2] = distance;
            distancies[factory2][factory1] = distance;
        }

        BasicInfo factories[] = new BasicInfo[factoryCount];
        int atac_per_bomba;

        // game loop
        while (true) {
            atac_per_bomba = 0;
            int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)

            torn++;

            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();

                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
                int arg5 = in.nextInt();

                if(entityType.equals("FACTORY")){
                    factories[entityId] = new BasicInfo(arg1, arg2, arg3);
                }else if(entityType.equals("BOOMB") && arg1==-1){
                    // Move my troops every turn that bomb is in travel, from my planet with most population to a neighbourg
                    atac_per_bomba = 1;
                }else{
                    // Mirar que fem amb les tropes
                    // Sumar-les quan van camint d'un planeta (2nd round)
                }
            }

            List<Integer> my_factories = new ArrayList<Integer>();
            // Search my factories
            for(int i=0; i<factoryCount; i++){
                if(factories[i].propietari==1){
                    my_factories.add(i);
                }
            }

            String accions[] = new String[factoryCount/2];
            int scores[] = new int[factoryCount/2];

            for(int k=0;k<scores.length; k++){
                accions[k] = "WAIT";
                scores[k] = 1000;
            }

            String accio_tmp = "WAIT";
            int score_tmp = 1000;
            String accio_tmp2 = "WAIT";
            int score_tmp2 = 1000;
            String accio_tmp3 = "WAIT";
            int score_tmp3 = 1000;
            int aux;
            int tropes = 1;
            List<Integer> max_tropes_meves = new ArrayList<Integer>();

            if(atac_per_bomba==1){
                // Search my factory with more troops
                max_tropes_meves = max_planeta_tropes(factories, 1, new ArrayList<Integer>());
            }

            // Heurístiques
            for(int i=0; i<my_factories.size(); i++){

                // If we have 0 troops in a factory, we don't need to do nothing with this planet
                if(factories[my_factories.get(i)].tropes > 0){

                    List<Integer> veins = veins(my_factories.get(i), 2, factories);
                    
                    accio_tmp = "WAIT";
                    score_tmp = 1000;
                    accio_tmp2 = "WAIT";
                    score_tmp2 = 1000;
                    accio_tmp3 = "WAIT";
                    score_tmp3 = 1000;

                    for(int j=0; j<veins.size(); j++){
                        List<Integer> cami = distancia_min(my_factories.get(i), veins.get(j), 3);

                        aux = score_planeta(my_factories.get(i), veins.get(j), cami.get(cami.size()-1), factories);

                        // En aquest cas li donem major prioritat a meoure aquestes tropes a qualsevol lloc
                        if(max_tropes_meves.size()>1 && max_tropes_meves.get(1) == i && aux!=1000){
                            if(aux>5){
                                aux -= 5;
                            }
                            else{
                                aux = 1;
                            }
                        }

                        if(aux!=1000 && aux < score_tmp){

                            if(aux < score_tmp3 && aux > score_tmp2){
                                score_tmp3 = aux;
                                tropes = 1;
                                if(factories[veins.get(j)].propietari==0){
                                    tropes = factories[veins.get(j)].tropes + 2;
                                }
                                else{
                                    tropes = (cami.get(cami.size()-1) * factories[veins.get(j)].produccio) + factories[veins.get(j)].tropes + 2;
                                }

                                accio_tmp3 = "MOVE " + my_factories.get(i) + " " + veins.get(j) + " " + tropes;
                            }
                            else if(aux < score_tmp2 && aux > score_tmp){
                                score_tmp3 = score_tmp2;
                                accio_tmp3 = accio_tmp2;

                                score_tmp2 = aux;
                                tropes = 1;
                                if(factories[veins.get(j)].propietari==0){
                                    tropes = factories[veins.get(j)].tropes + 2;
                                }
                                else{
                                    tropes = (cami.get(cami.size()-1) * factories[veins.get(j)].produccio) + factories[veins.get(j)].tropes + 2;
                                }

                                accio_tmp2 = "MOVE " + my_factories.get(i) + " " + veins.get(j) + " " + tropes;                            
                            }
                            else if(aux < score_tmp){

                                score_tmp3 = score_tmp2;
                                accio_tmp3 = accio_tmp2;
                                score_tmp2 = score_tmp;
                                accio_tmp2 = accio_tmp;

                                score_tmp = aux;
                                tropes = 1;
                                if(factories[veins.get(j)].propietari==0){
                                    tropes = factories[veins.get(j)].tropes + 2;
                                }
                                else{
                                    tropes = (cami.get(cami.size()-1) * factories[veins.get(j)].produccio) + factories[veins.get(j)].tropes + 2;
                                }

                                accio_tmp = "MOVE " + my_factories.get(i) + " " + veins.get(j) + " " + tropes;
                            }
                        }
                        else if(score_tmp == 1000 && aux==1000){
                            accio_tmp = "WAIT";
                        }
                    }

                    if(!accio_tmp.equals("WAIT")){

                        // If we have multiple troops in a planet, we allow to three actions
                        if(factories[my_factories.get(i)].tropes>15){
                            if(!accio_tmp2.equals("WAIT")){
                                accio_tmp += ";" + accio_tmp2;
                            }
                            if(!accio_tmp3.equals("WAIT")){
                                accio_tmp += ";" + accio_tmp3;
                            }
                        }

                        if(score_tmp < scores[0]){
                            for(int k=scores.length-1;k>0; k--){
                                if(!accions[k-1].equals("WAIT")){
                                    scores[k]=scores[k-1];
                                    accions[k]=accions[k-1];
                                }
                            }
                            scores[0] = score_tmp;
                            accions[0] = accio_tmp;     
                        }
                        else{
                            for(int k=scores.length-1;k>=1; k--){

                                if(score_tmp < scores[k] && score_tmp>scores[k-1]){

                                    for(int kk=scores.length-1;kk>k; kk--){
                                        if(!accions[kk-1].equals("WAIT")){
                                            scores[kk]=scores[kk-1];
                                            accions[kk]=accions[kk-1];
                                        }
                                    }

                                    scores[k] = score_tmp;
                                    accions[k] = accio_tmp;
                                    break;          
                                }
                            }
                        }
                    }
                }
            }
            

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            // Any valid action, such as "WAIT" or "MOVE source destination cyborgs"

            String accio = accions[0];

            for(int k=1;k<accions.length; k++){
                if(!accions[k].equals("WAIT")){
                    accio += ";" + accions[k];
                }
                else{
                    break;
                }
            }

            // Final bomb
            if(torn>180 && num_bombes>0){
                List<Integer> max_tropes = max_planeta_tropes(factories, -1, new ArrayList<Integer>());
                if(max_tropes.size()>1 && max_tropes.get(1)>10){
                    List<Integer> veins_desti = veins(max_tropes.get(0), 1, factories);

                    // We could calculate which neighbourg is more near, but would be to much predictive
                    // Then I send it from random neighbourg
                    if(veins_desti.size()>0){
                        Random rand = new Random();
                        int orig_aleatori = rand.nextInt(veins_desti.size());
                        accio += ";BOMB " + veins_desti.get(orig_aleatori) + " " + max_tropes.get(0);
                        num_bombes--;
                    }
                }
            }
            // We search for enemy planets with a lot of troops
            else if(num_bombes>0){
                List<Integer> max_tropes = max_planeta_tropes(factories, -1, new ArrayList<Integer>());

                // If same planet have more than 20 troops is a good candidate
                if(max_tropes.size()>1 && max_tropes.get(1)>20 && factories[max_tropes.get(0)].produccio>1){
                    List<Integer> veins_desti = veins(max_tropes.get(0), 1, factories);

                    // We could calculate which neighbourg is more near, but would be to much predictive
                    // Then I send it from random neighbourg
                    if(veins_desti.size()>0){
                        Random rand = new Random();
                        int orig_aleatori = rand.nextInt(veins_desti.size());
                        accio += ";BOMB " + veins_desti.get(orig_aleatori) + " " + max_tropes.get(0);
                        num_bombes--;
                    }
                }
            }

            System.out.println(accio);
        }
    }

    // tipus = 3 -> qualsevol tipus
    // tipus = 2 -> neutrals o enemics
    // tipus = 1 -> meus
    // tipus = 0 -> neutrals
    // tipus = -1 -> enemics
    static List<Integer> veins(int origen, int tipus, BasicInfo[] factories){

        List<Integer> veins = new ArrayList<Integer>();

        for(int i=0; i<factoryCount; i++){
            if(distancies[origen][i]!=1000){
                if( (tipus<2 && tipus==factories[i].propietari) || tipus==3){
                    veins.add(i);
                }
                else if(tipus==2 && (factories[i].propietari==-1 || factories[i].propietari==0) ){
                    veins.add(i);
                }
            }
        }

        return veins;
    }


    // Retornem el camíí méés curt entre origen i destí, i l'últim element q retornem a la llista es la logitud
    static List<Integer> distancia_min(int origen, int desti, int lvl){

        int min = distancies[origen][desti];
        List<Integer> cami = new ArrayList<Integer>();

        cami.add(origen);
        cami.add(desti);

        // Limitem a 3 nivells de profunditat
        if(lvl<3){
            for(int i=0; i<factoryCount; i++){
                if(distancies[origen][i]!=0){
                    List<Integer> aux = distancia_min(i, desti, lvl+1);
                    if(min > aux.get(aux.size()-1) + distancies[origen][i]){
                        min = aux.get(aux.size()-1) + distancies[origen][i];
                        cami.remove(cami.size()-1);
                        cami.addAll(aux);
                    }
                }
            }
        }

        // L'últim element q retornem es la logitud
        cami.add(min);

        return cami;
    }


    static int score_planeta(int origen, int desti, int distancia, BasicInfo[] factories){    
        int score;

        if(factories[desti].propietari == -1){
            if( (distancia * factories[desti].produccio) + factories[desti].tropes > factories[origen].tropes){
                score = 1000;
            }
            else{
                score = ((distancia * factories[desti].produccio) + factories[desti].tropes) - (factories[desti].produccio*3) + distancia;

                // Si és de l'enemic enlloc de neutral, li donem un plus
                score--;
            }
        }
        else if(factories[desti].propietari == 0){
            if(factories[desti].tropes > factories[origen].tropes){
                score = 1000;
            }
            else{
                score = factories[desti].tropes - (factories[desti].produccio*3) + distancia;
            }
        }
        // No passarà mai
        else{
            score = 1000;
        }

        return score;
    }

    // si frinedly = 1 -> planetes meus
    // si frinedly = -1 -> planetes enemics
    // descartar -> Listat d'identificadors a no tenir en compte (probablement pq hi estem fent altres accions ja)
    // Retorna id, num_tropes
    static List<Integer> max_planeta_tropes(BasicInfo[] factories, int friendly, List<Integer> descartar){    
        List<Integer> retornar = new ArrayList<Integer>();
        int max_tropes = 0;
        int max_id = -1;

        for(int i=0; i<factoryCount; i++){
            if(factories[i].propietari == friendly && !descartar.contains(i) && max_tropes<factories[i].tropes ){
                max_tropes = factories[i].tropes;
                max_id = i;
            }
        }

        retornar.add(max_id);
        retornar.add(max_tropes);

        return  retornar;
    }
}
