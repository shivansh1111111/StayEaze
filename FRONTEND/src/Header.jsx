import { Link } from "react-router-dom";
import { UserContext } from "./UserContext.jsx";
import { useContext, useState, useEffect, useRef } from "react";
import api from "./utils/axios";

export default function Header() {
    const {user} = useContext(UserContext);

    // Search states
    const [searchTerm, setSearchTerm] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const [showSuggestions, setShowSuggestions] = useState(false);
    const [loading, setLoading] = useState(false);
    const searchRef = useRef(null);
    const debounceTimer = useRef(null);

    // Close suggestions when clicking outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (searchRef.current && !searchRef.current.contains(event.target)) {
                setShowSuggestions(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    // Debounced autocomplete search
    useEffect(() => {
        if (debounceTimer.current) {
            clearTimeout(debounceTimer.current);
        }

        if (searchTerm.trim().length >= 2) {
            setLoading(true);
            debounceTimer.current = setTimeout(() => {
                fetchSuggestions(searchTerm);
            }, 300);
        } else {
            setSuggestions([]);
            setShowSuggestions(false);
            setLoading(false);
        }

        return () => {
            if (debounceTimer.current) {
                clearTimeout(debounceTimer.current);
            }
        };
    }, [searchTerm]);

    const fetchSuggestions = async (keyword) => {
        try {
            const response = await api.get(
                `/businessCompany/places/autocomplete?keyword=${encodeURIComponent(keyword)}`
            );

            // Extract places array from response
            const placesData = response.data.places || [];
            console.log('Fetched suggestions:', placesData); // Debug log
            setSuggestions(placesData);
            setShowSuggestions(placesData.length > 0);
        } catch (error) {
            console.error('Error fetching suggestions:', error);
            setSuggestions([]);
        } finally {
            setLoading(false);
        }
    };

    const handleLinkClick = () => {
        setSearchTerm('');
        setShowSuggestions(false);
        setSuggestions([]);
    };

    return (
        <div className="gap-1">
            <header className='flex justify-between'>
                <Link to={'/'} href="" className="flex items-center gap-1">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none"
                         strokeWidth={1.5} stroke="currentColor" className="w-8 h-8 text-red-600">
                        <path strokeLinecap="round" strokeLinejoin="round" d="M3 20h18l-6-10-3 5-3-5-6 10z"/>
                        <circle cx="18" cy="6" r="2"/>
                    </svg>
                    <span className='font-bold text-xl text-center sm:text-left'>stayeaze </span>
                </Link>

                {/* Search Bar with Autocomplete */}
                <div ref={searchRef} className='relative flex-1 max-w-md mx-4' style={{zIndex: 1000}}>
                    <div className='flex gap-2 border border-gray-300 rounded-full py-2 px-4 shadow-md shadow-gray-300 bg-white'>
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onFocus={() => suggestions.length > 0 && setShowSuggestions(true)}
                            placeholder="Search places..."
                            className='flex-1 outline-none bg-transparent text-sm'
                        />
                        <button className='bg-primary text-white p-1 rounded-full'>
                            {loading ? (
                                <svg className="animate-spin w-4 h-4" xmlns="http://www.w3.org/2000/svg"
                                     fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10"
                                            stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor"
                                          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                            ) : (
                                <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                                     strokeWidth={1.5} stroke="currentColor" className="w-4 h-4">
                                    <path strokeLinecap="round" strokeLinejoin="round"
                                          d="M21 21l-5.197-5.197m0 0A7.5 7.5 0 105.196 5.196a7.5 7.5 0 0010.607 10.607z" />
                                </svg>
                            )}
                        </button>
                    </div>

                    {/* Suggestions Dropdown */}
                    {showSuggestions && suggestions.length > 0 && (
                        <div className="absolute left-0 right-0 top-full mt-2 bg-white rounded-2xl shadow-xl
                                      border border-gray-300 max-h-96 overflow-y-auto"
                             style={{zIndex: 9999}}>
                            <ul className="py-2">
                                {suggestions.map((place) => (
                                    <Link
                                        key={place.id}
                                        to={'/place/' + place.id}
                                        onClick={handleLinkClick}
                                        className="block px-4 py-3 hover:bg-gray-50 transition-colors
                                                 border-b border-gray-100 last:border-b-0"
                                    >
                                        <div className="flex items-center gap-3">
                                            {/* Small Image */}
                                            <div className="w-12 h-12 bg-gray-200 rounded-lg flex-shrink-0 overflow-hidden">
                                                {place.imageUrl ? (
                                                    <img
                                                        src={place.imageUrl}
                                                        alt={place.title}
                                                        className="w-full h-full object-cover"
                                                    />
                                                ) : (
                                                    <div className="w-full h-full flex items-center justify-center">
                                                        <svg xmlns="http://www.w3.org/2000/svg" fill="none"
                                                             viewBox="0 0 24 24" strokeWidth={1.5}
                                                             stroke="currentColor" className="w-6 h-6 text-gray-400">
                                                            <path strokeLinecap="round" strokeLinejoin="round"
                                                                  d="M15 10.5a3 3 0 11-6 0 3 3 0 016 0z" />
                                                            <path strokeLinecap="round" strokeLinejoin="round"
                                                                  d="M19.5 10.5c0 7.142-7.5 11.25-7.5 11.25S4.5 17.642 4.5 10.5a7.5 7.5 0 1115 0z" />
                                                        </svg>
                                                    </div>
                                                )}
                                            </div>

                                            {/* Place Name */}
                                            <div className="flex-1 min-w-0">
                                                <p className="text-sm font-medium text-gray-900 truncate">
                                                    {place.title}
                                                </p>
                                            </div>
                                        </div>
                                    </Link>
                                ))}
                            </ul>
                        </div>
                    )}

                    {/* No Results */}
                    {showSuggestions && searchTerm.length >= 2 && suggestions.length === 0 && !loading && (
                        <div className="absolute left-0 right-0 top-full mt-2 bg-white rounded-2xl shadow-xl
                                      border border-gray-300 p-4"
                             style={{zIndex: 9999}}>
                            <p className="text-sm text-gray-500 text-center">No places found</p>
                        </div>
                    )}
                </div>

                <Link to={user?'/account': '/login'}
                      className='flex items-center gap-2 border border-gray-300 rounded-full py-2 px-4'>
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"
                         strokeWidth="1.5" stroke="currentColor" className="w-6 h-6">
                        <path strokeLinecap="round" strokeLinejoin="round"
                              d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
                    </svg>
                    <div className='bg-gray-500 text-white rounded-full border border-gray-500 overflow-hidden'>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor"
                             className="w-6 h-6 relative top-1">
                            <path fillRule="evenodd"
                                  d="M7.5 6a4.5 4.5 0 119 0 4.5 4.5 0 01-9 0zM3.751 20.105a8.25 8.25 0 0116.498 0 .75.75 0 01-.437.695A18.683 18.683 0 0112 22.5c-2.786 0-5.433-.608-7.812-1.7a.75.75 0 01-.437-.695z"
                                  clipRule="evenodd" />
                        </svg>
                    </div>
                    {!!user && (
                        <div>
                            {user.name}
                        </div>
                    )}
                </Link>
            </header>
        </div>
    );
}