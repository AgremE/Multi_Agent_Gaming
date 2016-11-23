package factoryFacility;

import java.awt.Desktop.Action;
import java.util.ArrayList;
import java.util.Hashtable;

import pomcpUtili.QNode;
import pomcpUtili.VNode;

public class FactorFacilityUtili {

	int limitedLenghtQNode = 10000;
	int limitedLenghtVNode = 1000;
	ArrayList<QNode> QNodeFactory;
	ArrayList<VNode> VNodeFactory;
	//init value into factoryFacility
	public FactorFacilityUtili(){
		this.QNodeFactory = new ArrayList<>();
		this.VNodeFactory = new ArrayList<>();
	}
	// check the qnode list availability
	public boolean checkQNodeFatoryEmpty(){
		return QNodeFactory.isEmpty();
	}
	// check the vnode list availability
	public boolean checkVNodeFactoryEmpty(){
		return VNodeFactory.isEmpty();
	}
	// add element into the qnode list 
	public void pushQnode(QNode qnode){
		
		QNodeFactory.add(qnode);
	}
	// add element into the vnode list
	public void pushVnode(VNode vnode){
		
		VNodeFactory.add(vnode);
	}
	// get an element from the qnode list
	public QNode popQnode(int[] action){
		
		if(QNodeFactory.isEmpty()){
			
			return null;
		}
		QNode node =  setQnodeToInitialCondition(QNodeFactory.get(0),action);
		QNodeFactory.remove(0);
		return node;
	}
	// get an element from the vnode list assumming every element in the list are the same
	public VNode popVnode(int[] observation){
		
		if(VNodeFactory.isEmpty()){
			return null;
		}
		VNode node = setVnodeToInitialConsidtion(VNodeFactory.get(0),observation);
		VNodeFactory.remove(0);
		return node;
	}
	
	public QNode setQnodeToInitialCondition(QNode qnode,int[] action){
		qnode.setValue(0.0, 0);
		qnode.Children.clear();
		if(qnode.Children == null){
			qnode.Children = new Hashtable<>();
		}
		qnode.setAction(action);
		return qnode;
	}
	
	public VNode setVnodeToInitialConsidtion(VNode vnode,int[] observation){
		
		vnode.setValue(0.0, 0);
		// TODO: Change the push into the list 
		vnode.Children.clear();
		if(vnode.Children == null){
			vnode.Children = new Hashtable<>();
		}
		vnode.initVNodeFromFactory(vnode);
		vnode.setObservation(observation);
		
		return vnode;
	}
	
	public void pushAllQnode(VNode vnode){
		if(vnode.Children.isEmpty()){
			return;
		}
		for(int keys : vnode.Children.keySet()){
			QNode qnode = vnode.Children.get(keys);
			pushQnode(qnode);
			pushAllVnode(qnode);
		}
		return;
	}
	
	public void pushAllVnode(QNode qnode){
		if(qnode.Children.isEmpty()){
			return;
		}
		for(int keys : qnode.Children.keySet()){
			VNode vnode = qnode.Children.get(keys);
			pushVnode(vnode);
			pushAllQnode(vnode);
		}
		return;
	}
}

