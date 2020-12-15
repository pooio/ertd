import axios from '../api'

export async function getTreeNode() {
    var arr = [];
    const res = await axios.post('sys/dictInfo/treeData')
    res.data.data[0].children.forEach(element => {
        arr.push({
            label: element.dicName,
            value: element.dicCode
        })
    });
    return arr;
}
export async function getUserList() {
    var arr = [];
    const res = await axios.post('sys/user/listAll')
    res.data.data.forEach(element => {
        arr.push({
            label: element.orgName,
            value: element.id
        })
    });
    return arr;
}
export async function getDictList(val) {
    var arr = [];
    const res = await axios.post('sys/dictInfo/getDictList', { dicCode: val })
    res.data.data.forEach(element => {
        arr.push({
            label: element.dicName,
            value: element.dicCode
        })
    });
    return arr;
}
export async function getDataList() {
    var arr = [];
    const res = await axios.post('formData/getIsPublishCustomList', {
    })
    res.data.data.forEach(element => {
        arr.push({
            label: element['businessName'],
            value: element.id
        })
    });
    return arr;
}
export async function getDeptList(val) {
    const res = await axios.post('sys/org/treeData')
    return orgRecursion(res.data.data);
}
function orgRecursion(item) {
    var arrResult = [];
    item.forEach(element => {
        var arr;
        if (element.children == null) {
            arr = {
                id: element.id,
                value: element.orgName,
                label: element.orgName,
            }
        } else {
            arr = {
                id: element.id,
                value: element.orgName,
                label: element.orgName,
                children: orgRecursion(element.children)
            }
        }
        arrResult.push(arr);
    });
    return arrResult;
}
