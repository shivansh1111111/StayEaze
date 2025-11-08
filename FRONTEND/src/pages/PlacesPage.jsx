import {useContext, useEffect, useState} from "react";
import { Link } from "react-router-dom";
import api from "../utils/axios";
import AccountNav from "../AccountNav";
import PlaceImg from "../PlaceImg";
import { UserContext } from "../UserContext.jsx";


export default function PlacesPage() {
    // const {action} = useParams();
    // const [redirectToPlaceList,setRedirectToPlaceList] = useState('');
    // //Redirect still redirect to account/places and navigates to the same page
    // if(redirectToPlaceList && action!=='new') {
    //     return <Navigate to={'/account/places'} />
    // }
    const {user} = useContext(UserContext);
    const [places,setPlaces] = useState([]);
    useEffect(() => {
        if (!user) {
            return <div>Loading your account...</div>;
        }

        api.get(`/businessCompany/user-places?userId=${user.userId}`)
            .then(({ data }) => {
                setPlaces(data.places);
            })
            .catch(err => console.error(err));
    }, [user]);


    return (
        <div>
            <AccountNav/>
            <div className="text-center">
                <br/>
                <Link className="inline-flex gap-2 bg-primary text-white px-6 py-2 rounded-full"
                      to={'/account/places/new'}>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={1.5}
                         stroke="currentColor" className="w-6 h-6">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M12 4.5v15m7.5-7.5h-15"/>
                    </svg>
                    Add new Place
                </Link>
            </div>
            <div className="mt-4">
                {places.length > 0 && places.map(place => (
                    <Link
                        key={place._id}
                        to={'/account/places/' + place._id}
                        className="flex cursor-pointer gap-4 bg-gray-100 p-4 rounded-2xl items-start"
                    >
                        <div className="w-48 h-32 bg-gray-300 flex-shrink-0 rounded-xl overflow-hidden">
                            <PlaceImg place={place} className="w-full h-full object-cover"/>
                        </div>
                        <div className="flex flex-col justify-start overflow-hidden">
                            <h2 className="text-xl font-semibold truncate">{place.title}</h2>
                            <p className="text-sm mt-2 line-clamp-3">{place.description}</p>
                        </div>
                    </Link>
                ))}
            </div>


        </div>
    );
}

